package com.cqsr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.cqsr.event.ProductEvent;
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
		
		if(productEvent != null) {
			product = productEvent.getProduct();
			type = productEvent.getType();
			
			if(type.equals("createProduct")) {
				productRepository.save(product);
			}
		}
	}
}
