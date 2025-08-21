//package com.cqsr.security;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//
//import reactor.core.publisher.Mono;
//
//@Component
//public class AudienceValidationFilter implements WebFilter {
//
//	private final Logger logger = LoggerFactory.getLogger(AudienceValidationFilter.class);
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth instanceof JwtAuthenticationToken jwtAuth) {
//            Jwt jwt = jwtAuth.getToken();
//            List<String> audience = jwt.getAudience();
//            if (audience == null || !audience.contains("api-gateway")) {
//            	logger.warn("Access denied: JWT audience missing or invalid");
//                return Mono.error(new AccessDeniedException("Invalid audience"));
//            }
//        }
//        return chain.filter(exchange);
//    }
//}
