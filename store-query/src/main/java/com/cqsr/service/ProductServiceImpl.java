package com.cqsr.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.cqsr.dto.ProductResponse;
import com.cqsr.event.ProductEvent;
import com.cqsr.exception.ProductNotFoundException;
import com.cqsr.mapper.ProductMapper;
import com.cqsr.model.Product;
import com.cqsr.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductMapper mapper;

	@KafkaListener(topics = "pro-topic1", groupId = "pro-group1")
	public void processStudentEvents(ProductEvent productEvent) {
		Product product = null;
		String type = null;

		if (productEvent != null) {
			product = productEvent.getProduct();
			type = productEvent.getType();

			logger.info("product: {}, action Type: {}", product, type);

			switch (type) {
			case "createProduct" -> {
				product.setProductId(null);
				productRepository.save(product);
				logger.info("product saved successfully in DB!");
			}
			case "updateProduct" -> {
				var existProduct = getProductById(product.getProductId());

				existProduct.setProductName(product.getProductName());
				existProduct.setCategory(product.getCategory());
				existProduct.setQuantity(product.getQuantity());
				existProduct.setPrice(product.getPrice());
				productRepository.save(existProduct);
				logger.info("product updated and saved successfully in DB!");
			}
			case "deleteProduct" -> {
				Product productToBeleted = getProductById(product.getProductId());
				productRepository.deleteById(productToBeleted.getProductId());
				logger.info("Product with ID -> " + product.getProductId() + " deleted from the consumer service");
			}
			}
		}
	}

	@Override
	public Product getProductById(Long productId) {
		var existProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product with ID -> " + productId + " not found!",
						HttpStatus.NOT_FOUND));
		return existProduct;
	}

	@Override
	public List<ProductResponse> getAllProducts() {
		List<Product> products = productRepository.findAll();
		return products.stream().map(product -> mapper.toResponse(product)).toList();
	}
}
