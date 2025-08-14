package com.miaoubich.service;

import com.miaoubich.dto.AuthenticationRequest;
import com.miaoubich.dto.AuthenticationResponse;
import com.miaoubich.dto.RegisterRequest;

public interface AuthenticationService {

	AuthenticationResponse register(RegisterRequest request);

	AuthenticationResponse authenticate(AuthenticationRequest request);

}
