package com.longvu.storage_service.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    // List of public endpoints that do not require authentication
    private static final String[] PUBLIC_ENDPOINTS = {"/*", "/assets/*", "/files", "/api/auth/**", "/api/storage/**"};

    // This method configures the security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Allow all requests to the public endpoints
        http.authorizeHttpRequests(
                request -> request.requestMatchers(PUBLIC_ENDPOINTS).permitAll());

        // Require authentication for all other requests
        http.authorizeHttpRequests(request -> request.anyRequest().authenticated());

        // Configure the OAuth2 resource server to use the custom JWT decoder and authentication entry point
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        // Disable CSRF protection
        http.csrf(AbstractHttpConfigurer::disable);

        // Configure CORS to allow all origins and certain methods and headers
        http.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTION"));
            configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configuration.setExposedHeaders(List.of("x-auth-token"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });

        // Build and return the security filter chain
        return http.build();
    }
}
