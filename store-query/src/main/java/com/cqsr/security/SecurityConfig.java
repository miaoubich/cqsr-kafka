package com.cqsr.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	            .anyRequest().access((authentication, context) -> {
	                HttpServletRequest request = context.getRequest();
	                String gatewayHeader = request.getHeader("X-Gateway-Auth");
	                if ("ali-query".equals(gatewayHeader)) {
	                    return new AuthorizationDecision(true);
	                }
	                return new AuthorizationDecision(false);
	            })
	        )
	        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

	    return http.build();
	}
}
