package com.cqsr.service;

import com.cqsr.dto.ProductRequest;
import com.cqsr.dto.ProductResponse;

public interface ProductService {

	ProductResponse addNewProduct(ProductRequest productRequest);
	ProductResponse editProductById(Long productId, ProductRequest productRequest);
	void deleteProduct(Long productId);
}
