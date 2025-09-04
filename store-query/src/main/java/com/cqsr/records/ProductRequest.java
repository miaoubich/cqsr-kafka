package com.cqsr.records;

import org.springframework.http.HttpStatus;

import com.cqsr.exception.PriceValueException;
import com.cqsr.exception.ProductQuantityNotEnoughException;

public record ProductRequest(
		String productName, 
		String category, 
		Integer quantity, 
		Double price
	) {
	public ProductRequest{
		if(quantity != null || quantity < 0) {
			throw new ProductQuantityNotEnoughException("Product quantity must not be null or negative value",
					HttpStatus.BAD_REQUEST);
		}
		if(price != null && price < 0) {
			throw new PriceValueException("Product price must not be null or negative value", 
					HttpStatus.BAD_REQUEST);
		}
	}
}
