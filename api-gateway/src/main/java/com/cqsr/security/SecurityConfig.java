package com.cqsr.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeExchange(exchanges -> 
								exchanges.anyExchange().authenticated())
			.oauth2ResourceServer(oauth2 -> 
									oauth2.jwt(jwt -> {}));

		return http.build();
	}
}
