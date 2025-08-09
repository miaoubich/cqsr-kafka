package com.cqsr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cqsr.model.Product;
import com.cqsr.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final static Logger logger = LoggerFactory.getLogger(ProductController.class);
	@Autowired
	private ProductService productService;
	
	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody Product product){
		Product result = productService.addNewProduct(product);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
}
