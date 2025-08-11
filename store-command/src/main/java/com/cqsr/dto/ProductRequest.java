package com.cqsr.dto;

public class ProductRequest {
	
    private String productName;
    private String category;
    private Integer quantity;
    private Double price;

    public ProductRequest() {}

    public ProductRequest(String productName, String category, Integer quantity, Double price) {
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
}