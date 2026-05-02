package com.velas.candil.controllers;

import com.velas.candil.models.user.AuthLoginDto;
import com.velas.candil.models.user.AuthRegisterDto;
import com.velas.candil.models.user.AuthResponseDto;
import com.velas.candil.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Operations related to authentication (Login - register - validation)")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Authenticate user",
            description = "Validates user credentials and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Malformed request")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
                            @Parameter(description = "User credentials", required = true)
                            @RequestBody AuthLoginDto authLoginDto){
        return ResponseEntity.ok(authenticationService.login(authLoginDto.username(), authLoginDto.password()));
    }

    @Operation(
            summary = "Register user",
            description = "Register a new user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Register successful"),
            @ApiResponse(responseCode = "400", description = "Malformed request")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Parameter(description = "User information for register", required = true)
            @RequestBody @Valid AuthRegisterDto authRegisterDto) throws MessagingException {
        authenticationService.register(authRegisterDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Validate activation token",
            description = "Activates a user account using the token sent by email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or malformed token"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "409", description = "Account already activated")
    })
    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(
            @Parameter(description = "Activation token sent to the user's email", required = true)
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
        return ResponseEntity.ok().build();
    }
}
