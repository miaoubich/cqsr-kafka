package com.cqsr.service;

import com.cqsr.records.ProductRequest;
import com.cqsr.records.ProductResponse;

public interface ProductService {

	ProductResponse addNewProduct(ProductRequest productRequest);
	ProductResponse editProductById(Long productId, ProductRequest productRequest);
	void deleteProduct(Long productId);
	void reduceProductQuantity(Long id, int amount);
}
