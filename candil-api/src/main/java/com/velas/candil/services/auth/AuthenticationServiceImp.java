package com.velas.candil.services.auth;

import com.velas.candil.config.jwt.JwtUtils;
import com.velas.candil.entities.user.ActivateToken;
import com.velas.candil.entities.user.Role;
import com.velas.candil.entities.user.User;
import com.velas.candil.exceptions.infra.MessagingErrorException;
import com.velas.candil.exceptions.roles.RoleNotFoundException;
import com.velas.candil.exceptions.users.*;
import com.velas.candil.mappers.UserMapper;
import com.velas.candil.models.user.AuthRegisterDto;
import com.velas.candil.models.user.AuthResponseDto;
import com.velas.candil.models.EmailTemplate;
import com.velas.candil.models.user.RoleEnum;
import com.velas.candil.models.user.UserInformationDto;
import com.velas.candil.repositories.ActivateTokenRepository;
import com.velas.candil.repositories.RoleRepository;
import com.velas.candil.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final ActivateTokenRepository tokenRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;
    private final UserDetailsService userDetailsService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Override
    public AuthResponseDto login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
            log.info("User logged in successfully: {}", username);
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", username);
            throw new InvalidCredentialsException("Invalid credentials " +e.getMessage());
        } catch (DisabledException e) {
            log.warn("Disabled account login attempt: {}", username);
            throw new AccountDisabledException("Account disabled " +e.getMessage());
        } catch (LockedException e) {
            log.warn("Locked account login attempt: {}", username);
            throw new AccountLockedException("Account locked " +e.getMessage());
        }
        User  user = (User) authentication.getPrincipal();
        String token = jwtUtils.generateToken(user);
        log.info("User logged in: {}", username);
        return new AuthResponseDto(token);
    }

    @Override
    @Transactional
    public void register(AuthRegisterDto dto) throws MessagingException, MailException {

        Optional<User> userOptional = userRepository.findByUsername(dto.username());
        if(userOptional.isPresent()){
            throw new UsernameAlreadyExistsException("Username "+dto.username()+" already exists. Try with another one.");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Role userRole = roleRepository.findByRole(RoleEnum.USER)
                .orElseThrow(() -> new RoleNotFoundException("Role with name "+ RoleEnum.USER +" not found"));

        user.getRoles().add(userRole);
        User userSaved;
        try {
            userSaved = userRepository.save(user);
        }catch (DataIntegrityViolationException ex) {
            // concurrent fallback
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        //TODO put exceptions into ControllerAdvice
        try {
            sendValidationEmail(userSaved);
        } catch (MessagingException e) {
            log.error("Error sending validation email to user: {}", userSaved.getEmail(), e);
            throw new MessagingErrorException("Error sending validation email" + e.getMessage());
        } catch (MailException e) {
            log.error("Error sending validation email to user: {}", userSaved.getEmail(), e);
            throw new MessagingErrorException("Error sending validation email");
        }

        log.info("User registered with username: {}", user.getUsername());
    }

    @Override
    public void sendValidationEmail(User user) throws MessagingException {
        String token = generateAndSaveToken(user);
        emailService.sendEmail(user.getEmail(), user.getUsername(), EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl, token, "Activate account");
    }

    @Override
    public String generateAndSaveToken(User user) {
        String generatedToken = generateToken(6);
        ActivateToken token = ActivateToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    @Override
    public String generateToken(int length) {
        String token = "1234567890";
        SecureRandom random = new SecureRandom();
        StringBuilder tokenSb = new StringBuilder();
        int indexRandom;
        for(int i=0;i<length;i++){
            indexRandom = random.nextInt(token.length());
            tokenSb.append(token.charAt(indexRandom));
        }
        return tokenSb.toString();
    }

    @Override
    public void activateAccount(String token) throws MessagingException {
        ActivateToken tokenResult = tokenRepository.findByToken(token).orElseThrow(() ->
                new TokenInvalidException("Token not valid. Make sure that's the token sent to your account."));

        if(LocalDateTime.now().isAfter(tokenResult.getExpiredAt())){
            sendValidationEmail(tokenResult.getUser());
            throw new TokenExpiredException("Activation token has expired. We have sent another one to your email.");
        }
        User user = tokenResult.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenResult.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(tokenResult);    }
}