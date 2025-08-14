//package com.miaoubich.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import com.miaoubich.repository.UserRepository;
//
//@Configuration
//public class ApplicationConfig {
//	
//	@Autowired
//	private UserRepository userRepository;
//
//	@Bean
//	public UserDetailsService userDetailsService() {
//		return username -> userRepository.findByEmail(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User not found!"));
//			
//	}
//	
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//	    authBuilder
//	        .userDetailsService(userDetailsService())
//	        .passwordEncoder(passwordEncoder());
//
//	    http
//	        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//	        .formLogin(Customizer.withDefaults());
//
//	    return http.build();
//	}
//
//	@Bean
//	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//	    return http.getSharedObject(AuthenticationManagerBuilder.class).build();
//	}
//
//	@Bean
//	private PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//}
