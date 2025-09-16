package com.miaoubich;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.cqsr.event.ProductEvent;
import com.cqsr.mapper.ProductMapper;
import com.cqsr.model.Product;
import com.cqsr.records.ProductResponse;
import com.cqsr.repository.ProductRepository;
import com.cqsr.service.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
public class ProductServiceImplTest {

	private final Logger logger = LoggerFactory.getLogger(ProductServiceImplTest.class);
	
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ProductMapper mapper;
	@InjectMocks
	private ProductServiceImpl productService;
	
	private Product product;
	private ProductEvent productEvent;
	
	@Test
	@DisplayName("processProductEvents should save product on createProduct event")
	void processProductEventsCreateProductTest() {
		// Arrange
		product = new Product(
				"Samsung Android TV",
				"Television",
				10,
				299.00);
		ReflectionTestUtils.setField(product, "productId", 99L);
		productEvent = new ProductEvent("createProduct", product);
		
		// Act
		productService.processProductEvents(productEvent);
		
		// Assert
		assertEquals(null, product.getProductId());
		verify(productRepository).save(product);
		verifyNoMoreInteractions(productRepository);
	}
	
	@Test
	@DisplayName("processProductEvents should update and save product on updateProduct event")
	void processProductEventsUpdateProductTest() {
		// Arrange
		Product existing = new Product("oldProductName", "oldCategory", 5, 50.00);
		ReflectionTestUtils.setField(existing, "productId", 1L);
		
		Product updated = new Product("newProductName", "newCategory", 10, 100.00);
		ReflectionTestUtils.setField(updated, "productId", 1L);
		
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
		
		ProductEvent productEvent = new ProductEvent("updateProduct", updated);
		
		// Act
		productService.processProductEvents(productEvent);
		
		// Assert
		assertEquals("newProductName", existing.getProductName());
		assertEquals("newCategory", existing.getCategory());
		assertEquals(10, existing.getQuantity());
		assertEquals(100.00, existing.getPrice());
		
		verify(productRepository).findById(1L);
		verify(productRepository).save(existing);
	} 
	
	@Test
	@DisplayName("processProductEvents should delete product on deleteProduct event")
	void processProductEventsDeleteProduct() {
		// Arrange
		Product existing = new Product("GS-22 plus", "Mobile", 1, 299.00);
		ReflectionTestUtils.setField(existing, "productId", 1L);
		
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
		
		ProductEvent productEvent = new ProductEvent("deleteProduct", existing);
		
		// Act
		productService.processProductEvents(productEvent);
		
		// Assert
		verify(productRepository).findById(1L);
		verify(productRepository).deleteById(1L);
	}
	
	@Test
	@DisplayName("processProductEvents should do nothing when event is null")
	void processProductEventsNullEvent() {
		// Act
		productService.processProductEvents(null);
		
		// Assert
		verifyNoInteractions(productRepository);
	}
	
	@Test
	@DisplayName("getProductById should retrieve a product by its productId")
	void getProductByIdTest() {
		// Arrange
		Long productId = 11L;
		Product product = new Product("Smart TV", "TV", 10, 259.0);
		ReflectionTestUtils.setField(product, "productId", productId);
		
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		// Act
		Product result = productService.getProductById(productId);
		
		// Assert
		assertNotNull(result);
		assertEquals(productId, result.getProductId());
		assertEquals("Smart TV", result.getProductName());
		assertEquals("TV", result.getCategory());
		assertEquals(10, result.getQuantity());
		assertEquals(259.0, result.getPrice());
		
		verify(productRepository).findById(productId);
	}
	
	@Test
	@DisplayName("getAllProducts should retrieve a list of products")
	void getAllProductsTest() {
		// Arrange
		var products = Arrays.asList(new Product("Smart TV", "TV", 10, 259.0),
								     new Product("Smart Phone", "Phone", 8, 359.0),
								     new Product("Smart Fridge", "Electronics", 15, 959.0));
		when(productRepository.findAll()).thenReturn(products);
		when(mapper.toResponse(any(Product.class)))
        .thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            return new ProductResponse(
            		p.getProductId(), 
            		p.getProductName(), 
            		p.getCategory(), 
            		p.getQuantity(), 
            		p.getPrice());
        });
		
		// Act
		var result = productService.getAllProducts();
		
		// Assert
		verify(productRepository).findAll();
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("Phone", result.get(1).category());
		assertEquals("Smart Fridge", result.get(2).productName());
		
	}
}
