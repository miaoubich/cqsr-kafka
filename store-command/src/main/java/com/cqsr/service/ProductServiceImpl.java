package com.cqsr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.cqsr.event.ProductEvent;
import com.cqsr.model.Product;
import com.cqsr.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
	
	@Value("${spring.kafka.template.default-topic}")
	private String topic;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private KafkaTemplate<String, ProductEvent> template;
	
	@Override
	public Product addNewProduct(Product product) {
		
		if(product != null) {
			Product savedProduct = productRepository.save(product);
			var event = new ProductEvent("createProduct", savedProduct);
			template.send(topic, event);
			logger.info("Product Event -> {} subscribed to the topic -> {}", event, topic);
		}
		return product;
	}
	
}
