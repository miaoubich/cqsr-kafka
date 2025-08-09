package com.cqsr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cqsr.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final static Logger logger = LoggerFactory.getLogger(ProductController.class);
	@Autowired
	private ProductService productService;
}
