package com.cqsr.service;

import com.cqsr.model.Product;

public interface ProductService {

	Product addNewProduct(Product product);
	Product editProductById(Long productId, Product product);
}
