package com.cqsr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.cqsr.dto.ProductRequest;
import com.cqsr.dto.ProductResponse;
import com.cqsr.event.ProductEvent;
import com.cqsr.exception.ProductNotFoundException;
import com.cqsr.mapper.ProductMapper;
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
	@Autowired
	private ProductMapper productMapper;

	@Override
	public ProductResponse addNewProduct(ProductRequest productRequest) {
		logger.info("Start addNewProduct() method");

		if (productRequest == null) {
			throw new IllegalArgumentException("ProductRequest must not be null !");
		}
		// Alternative to BeanUtils is MapStruct and it's more robust
		Product product = productMapper.toEntity(productRequest);
		Product savedProduct = productRepository.save(product);
		publishEvent("createProduct", savedProduct);

		return productMapper.toResponse(savedProduct);
	}

	@Override
	public ProductResponse editProductById(Long productId, ProductRequest productRequest) {
		logger.info("Start editProductById() method");

		Product existProduct = isProductExist(productId);
		logger.info("existProduct: {}", existProduct);

		Product product = productMapper.toEntity(productRequest);

		existProduct.setProductName(product.getProductName());
		existProduct.setCategory(product.getCategory());
		existProduct.setQuantity(product.getQuantity());
		existProduct.setPrice(product.getPrice());
		productRepository.save(existProduct);

		logger.info("existProduct ID: {}", existProduct.getProductId());
		publishEvent("updateProduct", existProduct);

		return productMapper.toResponse(existProduct);
	}

	@Override
	public void deleteProduct(Long productId) {
		Product productToBedeleted = isProductExist(productId);
		productRepository.deleteById(productId);
		logger.info("Product with ID {} successfully deleted!", productId);

		publishEvent("deleteProduct", productToBedeleted);
	}

	private Product isProductExist(Long productId) {
		Product existProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product with ID + " + productId + " not found!",
						HttpStatus.NOT_FOUND));
		return existProduct;
	}

	private void publishEvent(String action, Product product) {
		var event = new ProductEvent(action, product);
		template.send(topic, event);
		logger.info("ProductEvent '{}' published to topic '{}'", action, topic);
	}
}
