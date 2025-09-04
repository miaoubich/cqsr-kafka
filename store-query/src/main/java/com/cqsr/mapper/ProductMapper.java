package com.cqsr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cqsr.model.Product;
import com.cqsr.records.ProductRequest;
import com.cqsr.records.ProductResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	Product toEntity(ProductRequest request);
	ProductResponse toResponse(Product product);
}
