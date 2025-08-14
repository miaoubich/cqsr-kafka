package com.miaoubich.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.miaoubich.dto.AuthenticationRequest;
import com.miaoubich.dto.AuthenticationResponse;
import com.miaoubich.dto.RegisterRequest;
import com.miaoubich.mapper.UserMapper;
import com.miaoubich.repository.UserRepository;
import com.miaoubich.user.Role;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	

	public AuthenticationServiceImpl(UserRepository userRepository, UserMapper userMapper,
			PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public AuthenticationResponse register(RegisterRequest request) {
		var user = userMapper.toEntity(request);
		user.setPass(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.USER);
		
		userRepository.save(user);
		
		var jwtToken = jwtService.generateToken(user);
		var authenticationResponse = userMapper.toResponse(jwtToken);
		
		return authenticationResponse;  
	}

	@Override
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		
		var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var authenticationResponse = userMapper.toResponse(jwtToken);
		
		return authenticationResponse;
	}
}
