package com.cqsr.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        // Use the following cors since the front-end is separate
	        .cors(cors -> cors.configurationSource(request -> {
	            CorsConfiguration config = new CorsConfiguration();
	            config.setAllowedOrigins(List.of("http://localhost:3000"));
	            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
	            config.setAllowedHeaders(List.of("*"));
	            return config;
	        }))
	        .authorizeExchange(exchanges -> 
	            exchanges
	                .pathMatchers("/store-command/**").hasRole("admin")
	                .pathMatchers("/store-query/**")
			                .access((authMono, ctx) -> 
			                    authMono
			                        .doOnNext(auth -> logger.info(
			                            "SECURITY SAW PATH: {} | Authorities: {}",
			                            ctx.getExchange().getRequest().getPath(),
			                            auth.getAuthorities()
			                        ))
			                        .map(auth -> {
			                            boolean granted = auth.getAuthorities().stream()
			                                .anyMatch(a -> a.getAuthority().equals("ROLE_admin")
			                                            || a.getAuthority().equals("ROLE_user"));
			                            return new AuthorizationDecision(granted);
			                        })
			                )
	                .anyExchange().authenticated() // don't forget a fallback rule
	        	)
	        .oauth2ResourceServer(oauth2 -> 
	            oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
	        );

	    return http.build();
	}

	
	@Bean
	public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
	    ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
	    converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
	    return converter;
	}
	
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
	    return builder.routes()
	        .route("store_query_route", r -> r.path("/store-query/**")
	            .filters(f -> f
	            			.stripPrefix(1)
	            			.addRequestHeader("X-Gateway-Auth", "ali-query"))
	            .uri("lb://store-query"))
	        .build();
	}
}
