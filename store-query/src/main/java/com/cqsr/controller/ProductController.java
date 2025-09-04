package com.cqsr.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cqsr.mapper.ProductMapper;
import com.cqsr.records.ProductResponse;
import com.cqsr.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final static Logger logger = LoggerFactory.getLogger(ProductController.class);
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductMapper mapper;
	
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProduct(@PathVariable (name = "id") Long productId){
		ProductResponse result = mapper.toResponse(productService.getProductById(productId));
		return ResponseEntity.status(HttpStatus.FOUND).body(result);
	}
	
	@GetMapping
	public ResponseEntity<?> getAllProducts(){
		List<ProductResponse> result = productService.getAllProducts();
		return ResponseEntity.status(HttpStatus.FOUND).body(result);
	}
}
