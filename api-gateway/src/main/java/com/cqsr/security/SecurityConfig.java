package com.cqsr.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
	    http
	    	.exceptionHandling(ex -> ex
	    			.authenticationEntryPoint((exchange, e) -> 
	    						Mono.fromRunnable(() -> exchange
			    					.getResponse()
			    					.setStatusCode(HttpStatus.UNAUTHORIZED)
	    						)
	    			)
	    			.accessDeniedHandler((exchange, e) ->
	                Mono.fromRunnable(() ->
	                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
	                )
	            )
	    	)
	        .csrf(csrf -> csrf.disable())
	        .authorizeExchange(exchanges -> 
	            exchanges
	            	.pathMatchers("/internal/store-query/**").permitAll()
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
			         // Fallback: require authentication
	                .anyExchange().authenticated()
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
	        .route("store_command_route", r -> r.path("/store-command/**")
	        		.filters(f -> f
	        					.stripPrefix(1)
	        					.addRequestHeader("X-Gateway-Auth", "ali-command"))
	        		.uri("lb://store-command"))
	        .build();
	}
}
