package com.cqsr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cqsr.dto.ProductRequest;
import com.cqsr.dto.ProductResponse;
import com.cqsr.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	Product toEntity(ProductRequest request);
	ProductResponse toResponse(Product product);
}
