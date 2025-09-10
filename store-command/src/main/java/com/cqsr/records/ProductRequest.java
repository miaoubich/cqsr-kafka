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
		if(quantity != null && quantity < 0) {
			throw new ProductQuantityNotEnoughException("Product quantity must not be null or negative value",
					HttpStatus.BAD_REQUEST);
		}
		if(price != null && price < 0) {
			throw new PriceValueException("Product price must not be null or negative value", 
					HttpStatus.BAD_REQUEST);
		}
	}
	
	public static class Builder {
        private String productName;
        private String category;
        private Integer quantity;
        private Double price;

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public ProductRequest build() {
            return new ProductRequest(productName, category, quantity, price);
        }
	}

    public static Builder builder() {
        return new Builder();
    }

}
