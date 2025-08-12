package com.cqsr.service;

import java.util.List;

import com.cqsr.dto.ProductResponse;
import com.cqsr.model.Product;

public interface ProductService {

	Product getProductById(Long productId);
	List<ProductResponse> getAllProducts();

}
