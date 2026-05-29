package com.velas.candil.config.security;

import com.velas.candil.config.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityFilterConfig {

    @Value("${application.cors.allowed-origins}")
    private String allowedOrigins;
    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception {
        return https
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/v1/auth/**",
                                        "/images/**")
                                .permitAll()
                                //Candles
                                .requestMatchers(HttpMethod.GET, "/v1/candles/category/",
                                        "/v1/candles/feature",
                                        "/v1/candles/material",
                                        "/v1/candles/date",
                                        "/v1/candles/search",
                                        "/v1/candles/**",
                                        "/v1/candles").permitAll()

                                .requestMatchers(HttpMethod.POST, "/v1/candles").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/candles/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/candles/**").hasRole("ADMIN")
                                //Ingredients
                                .requestMatchers(HttpMethod.GET, "/v1/ingredients/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/v1/ingredients").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/v1/ingredients/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/v1/ingredients/**").hasRole("ADMIN")
                                //Cart
                                .requestMatchers("/v1/cart/**").authenticated()
                                // Orders — el webhook de MP debe ser público (MP no manda JWT)
                                .requestMatchers(HttpMethod.POST, "/v1/orders/webhook").permitAll()
                                .requestMatchers("/v1/orders/**").authenticated()
                                .anyRequest()
                                .authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.getWriter().write("Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            response.getWriter().write("Forbidden");
                        })
                )
                .build();
    }

    /*@Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        //config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");

        //config.addAllowedOriginPattern(allowedOrigins);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }*/

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("https://joylessly-dispersed-harmonics.ngrok-free.dev");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
