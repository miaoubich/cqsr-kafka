package com.cqsr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cqsr.dto.ProductRequest;
import com.cqsr.dto.ProductResponse;
import com.cqsr.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final static Logger logger = LoggerFactory.getLogger(ProductController.class);
	@Autowired
	private ProductService productService;

	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody ProductRequest product) {
		ProductResponse result = productService.addNewProduct(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<?> updateProduct(@PathVariable("productId") Long productId, 
									@RequestBody ProductRequest product) {
		ProductResponse result = productService.editProductById(productId, product);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId){
		productService.deleteProduct(productId);
		return ResponseEntity.ok("Product with ID -> " + productId + " successfully deleted!");
	}
	
	@PatchMapping("/{id}/reduce")
    public ResponseEntity<Void> reduceQuantity(@PathVariable Long id, @RequestParam int amount) {
		productService.reduceProductQuantity(id, amount);
		return new ResponseEntity<Void>(HttpStatus.OK);
		
	}
}
