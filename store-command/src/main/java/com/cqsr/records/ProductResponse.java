package com.cqsr.records;

public record ProductResponse(
		Long productId, 
		String productName, 
		String category, 
		Integer quantity, 
		Double price) {}
