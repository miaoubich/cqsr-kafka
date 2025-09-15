package com.miaoubich;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

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
import com.cqsr.repository.ProductRepository;
import com.cqsr.service.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
public class ProductServiceImplTest {

	private final Logger logger = LoggerFactory.getLogger(ProductServiceImplTest.class);
	
	@Mock
	private ProductRepository productRepository;
	@Mock
	private KafkaTemplate<String, ProductEvent> template;
	@Mock
	private ProductMapper productMapper;
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
}
