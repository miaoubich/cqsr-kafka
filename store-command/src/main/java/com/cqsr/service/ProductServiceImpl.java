  package com.cqsr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.cqsr.event.ProductEvent;
import com.cqsr.exception.ProductNotFoundException;
import com.cqsr.exception.ProductQuantityNotEnoughException;
import com.cqsr.mapper.ProductMapper;
import com.cqsr.model.Product;
import com.cqsr.records.ProductRequest;
import com.cqsr.records.ProductResponse;
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
		Product savedProduct = productRepository.save(existProduct);

		logger.info("Product with ID: {} was updated to {}", productId, savedProduct);
		publishEvent("updateProduct", existProduct);// or savedProduct

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
		Product existProduct = getProductById(productId);
		return existProduct;
	}

	private void publishEvent(String action, Product product) {
		var event = new ProductEvent(action, product);
		template.send(topic, event);
		logger.info("ProductEvent '{}' published to topic '{}'", action, topic);
	}

	@Override
	public void reduceProductQuantity(Long productId, int amount) {
		Product product = getProductById(productId);
		
		if(product.getQuantity() >= amount) {
			product.setQuantity(product.getQuantity()-amount);
			productRepository.save(product);
			publishEvent("updateProduct", product);
		}
		else throw new ProductQuantityNotEnoughException("Product amount exceed the product quantity available in our stock", HttpStatus.CONFLICT);
		
	}

	private Product getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product with ID -> " + id + " not found!",
						HttpStatus.NOT_FOUND));
		return product;
	}
}
