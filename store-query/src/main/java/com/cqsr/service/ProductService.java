package com.cqsr.service;

import java.util.List;

import com.cqsr.model.Product;
import com.cqsr.records.ProductResponse;

public interface ProductService {

	Product getProductById(Long productId);
	List<ProductResponse> getAllProducts();

}
