//package com.cqsr.security;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtException;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
//
//    @Override
//    public Collection<GrantedAuthority> convert(Jwt jwt) {
//        // Extract roles from realm_access.roles
//        Object realmAccess = jwt.getClaim("realm_access");
//        if (realmAccess instanceof Map) {
//            Map<?, ?> accessMap = (Map<?, ?>) realmAccess;
//            Object roles = accessMap.get("roles");
//            if (roles instanceof List) {
//                List<?> roleList = (List<?>) roles;
//                return roleList.stream()
//                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString()))
//                        .collect(Collectors.toList());
//            }
//        }
//
//        // Fallback: no roles found
//        return Collections.emptyList();
//    }
//}