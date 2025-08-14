package com.miaoubich.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.miaoubich.dto.AuthenticationResponse;
import com.miaoubich.dto.RegisterRequest;
import com.miaoubich.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	User toEntity(RegisterRequest request);
	
	AuthenticationResponse toResponse(String token);
	
	
}
