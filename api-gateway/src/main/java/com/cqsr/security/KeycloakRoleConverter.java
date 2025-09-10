package com.cqsr.security;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import reactor.core.publisher.Flux;

public class KeycloakRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

	private final Logger logger = LoggerFactory.getLogger(KeycloakRoleConverter.class);
	@Override
	public Flux<GrantedAuthority> convert(@NonNull Jwt jwt) {
		// the role can be extracted from realm_access or from resource_access
		Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");
		
		if(realmAccess != null && realmAccess.containsKey("roles")) {
			List<String> roles = realmAccess.get("roles");
			
			List<GrantedAuthority> authoritiesList = roles.stream()
					.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
					.collect(Collectors.toList());
			
			//authoritisList -> [ROLE_default-roles-bouzar, ROLE_offline_access, ROLE_admin, ROLE_uma_authorization]
			logger.info("authoritisList -> {}", authoritiesList);
			
			//authoritisList -> FluxIterable
			return Flux.fromIterable(authoritiesList);
		}
		return Flux.empty();
	}

}
