package com.cqsr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.cqsr.event.ProductEvent;
import com.cqsr.exception.ProductNotFoundException;
import com.cqsr.model.Product;
import com.cqsr.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductRepository productRepository;

	@KafkaListener(topics = "pro-topic1", groupId = "pro-group1")
	public void processStudentEvents(ProductEvent productEvent) {
		Product product = null;
		String type = null;

		if (productEvent != null) {
			product = productEvent.getProduct();
			type = productEvent.getType();
			
			logger.info("product: {}, action Type: {}", product, type);

			if (type.equals("createProduct")) {
				productRepository.save(product);
				logger.info("product saved successfully in DB!");
			} else if (type.equals("updateProduct")) {
				var existProduct = IsProductExist(product);

				existProduct.setProductName(product.getProductName());
				existProduct.setCategory(product.getCategory());
				existProduct.setQuantity(product.getQuantity());
				existProduct.setPrice(product.getPrice());
				productRepository.save(existProduct);
				logger.info("product updated and saved successfully in DB!");
			}
		}
	}

	private Product IsProductExist(Product product) {
		var existProduct = productRepository.findById(product.getProductId())
				.orElseThrow(() -> new ProductNotFoundException("Product with ID + " +
						product.getProductId() + " not found!", HttpStatus.NOT_FOUND));
		return existProduct;
	}
}
