package com.cqsr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public ResponseEntity<?> createProduct(@RequestBody Product product) {
		Product result = productService.addNewProduct(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<?> updateProduct(@PathVariable("productId") Long productId, 
									@RequestBody Product product) {
		Product result = productService.editProductById(productId, product);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId){
		productService.deleteProduct(productId);
		return ResponseEntity.ok("Product with ID -> " + productId + " successfully deleted!");
	}
}
