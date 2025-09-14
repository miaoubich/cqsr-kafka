package com.miaoubich;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.cqsr.event.ProductEvent;
import com.cqsr.exception.ProductQuantityNotEnoughException;
import com.cqsr.mapper.ProductMapper;
import com.cqsr.model.Product;
import com.cqsr.records.ProductRequest;
import com.cqsr.records.ProductResponse;
import com.cqsr.repository.ProductRepository;
import com.cqsr.service.ProductServiceImpl;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTests {

	private final Logger logger = LoggerFactory.getLogger(ProductServiceImplTests.class);

	@Mock
	private ProductRepository productRepository;
	@Mock
	private KafkaTemplate<String, ProductEvent> template;
	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductServiceImpl productService;

	private ProductRequest productRequest;
	private Product existingProduct;
	private ProductRequest updateRequest;
	private Product mappedProduct;
	private ProductResponse updatedResponse;

	@BeforeEach
	void setUp() {
		productRequest = ProductRequest.builder()
				.productName("Moto-15")
				.category("mobile")
				.quantity(19)
				.price(259.00)
				.build();
		// Inject Kafka topic into private field "topic" in ProductServiceImpl
		ReflectionTestUtils.setField(productService, "topic", "product-events");

		existingProduct = new Product("OldName", "OldCategory", 5, 100.00);
		ReflectionTestUtils.setField(existingProduct, "productId", 1L);

		updateRequest = ProductRequest.builder().productName("NewName").category("NewCategory").quantity(10)
				.price(200.00).build();
		mappedProduct = new Product("NewName", "NewCategory", 10, 200.0);
		updatedResponse = new ProductResponse(1L, "NewName", "NewCategory", 10, 200.0);
	}

	@Test
	@DisplayName("addNewProduct should throw IllegalArgumentException when productRequest is null")
	void addNewProduct_NullRequest_ThrowsException() {
		// Given
		String errorMessage = "ProductRequest must not be null !";

		// When + Then
		IllegalArgumentException illegalException = assertThrows(IllegalArgumentException.class,
				() -> productService.addNewProduct(null));
		assertEquals(errorMessage, illegalException.getMessage());

		// Verify that no repository or Kafka calls were made
		verifyNoInteractions(productRepository, template, productMapper);
	}

	@Test
	@DisplayName("addNewProduct should create a new product and publish a creat product event")
	void addProductSuccessTest() {
		// Given
		logger.info("productRequest -> {}", productRequest);

		Product mappedEntity = new Product("Moto-15", "Mobile", 19, 259.00);
		Product savedEntity = new Product("Moto-15", "Mobile", 19, 259.00);
		ReflectionTestUtils.setField(savedEntity, "productId", 1L);

		ProductResponse mappedResponse = new ProductResponse(1L, "Moto-15", "Mobile", 19, 259.00);

		// When
		when(productMapper.toEntity(productRequest)).thenReturn(mappedEntity);
		when(productRepository.save(mappedEntity)).thenReturn(savedEntity);
		when(productMapper.toResponse(savedEntity)).thenReturn(mappedResponse);

		final ProductResponse result = productService.addNewProduct(productRequest);
		logger.info("result -> {}", result);

		// Then
		assertNotNull(result);
		assertEquals("Moto-15", result.productName());
		assertEquals("Mobile", result.category());
		assertEquals(19, result.quantity());
		assertEquals(259.00, result.price());

		// Capture and verify event
		String injectedTopic = (String) ReflectionTestUtils.getField(productService, "topic");// read the calue of
																								// "topic" and assign it
																								// into [injectedTopic]
		ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);

		verify(template).send(eq(injectedTopic), eventCaptor.capture());

		ProductEvent productEvent = eventCaptor.getValue();
		assertEquals("createProduct", productEvent.getType());
		assertEquals("Moto-15", productEvent.getProduct().getProductName());
		assertEquals("Mobile", productEvent.getProduct().getCategory());
		assertEquals(19, productEvent.getProduct().getQuantity());
		assertEquals(259.00, productEvent.getProduct().getPrice());
	}

	@Test
    @DisplayName("editProductById should update product and publish update event")
    void editProductByIdSuccessTest() {
    	//Arrange
    	when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productMapper.toEntity(updateRequest)).thenReturn(mappedProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.toResponse(existingProduct)).thenReturn(updatedResponse);
    	
    	// Act
        ProductResponse result = productService.editProductById(1L, updateRequest);
    	
        // Assert returned response
        assertNotNull(result);
        assertEquals("NewName", result.productName());
        assertEquals("NewCategory", result.category());
        assertEquals(10, result.quantity());
        assertEquals(200.00, result.price());
        assertEquals(10, result.quantity());
        
     // Assert: repository interactions
        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);

        // Assert: mapper interactions
        verify(productMapper).toEntity(updateRequest);
        verify(productMapper).toResponse(existingProduct);

        // Capture and assert Kafka event
        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
        String injectedTopic = (String) ReflectionTestUtils.getField(productService, "topic");// read the calue of "topic" and assign it into [injectedTopic]
        verify(template).send(eq(injectedTopic), eventCaptor.capture());

        ProductEvent sentEvent = eventCaptor.getValue();
        assertEquals("updateProduct", sentEvent.getType());
        assertEquals("NewName", sentEvent.getProduct().getProductName());
        assertEquals("NewCategory", sentEvent.getProduct().getCategory());
        assertEquals(10, sentEvent.getProduct().getQuantity());
        assertEquals(200.0, sentEvent.getProduct().getPrice());
    }

	@Test
    @DisplayName("deleteProduct should delete product and publich delete event")
    void deleteProductTest() {
    	// Arrange
    	when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    	doNothing().when(productRepository).deleteById(1L);
    	
    	// Act
    	productService.deleteProduct(1L);
    	
    	// Assert repository interactions
    	verify(productRepository).findById(1L);
    	verify(productRepository).deleteById(1L);
    	
    	// Capture and assert Kafka
    	ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
    	verify(template).send(eq("product-events"), eventCaptor.capture());
    	
    	ProductEvent sentEvent = eventCaptor.getValue();
    	assertEquals("deleteProduct", sentEvent.getType());
        assertEquals(existingProduct.getProductName(), sentEvent.getProduct().getProductName());
        assertEquals(existingProduct.getCategory(), sentEvent.getProduct().getCategory());
        assertEquals(existingProduct.getQuantity(), sentEvent.getProduct().getQuantity());
        assertEquals(existingProduct.getPrice(), sentEvent.getProduct().getPrice());
    }
	
	@Test
	@DisplayName("reduceProductQuantity Have to successfully reduce the product quantity and publish a partial product event")
	void reduceProductTest() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
		when(productRepository.save(existingProduct)).thenReturn(existingProduct);
		
		// Act
		productService.reduceProductQuantity(1L, 3);
		
		// Assert repository interactions
        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
        
        // Assert quantity was reduced
        assertEquals(2, existingProduct.getQuantity());
        
        // Capture and assert Kafka event
        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(template).send(eq("product-events"), eventCaptor.capture());
        
        ProductEvent sentEvent = eventCaptor.getValue();
        assertEquals("updateProduct", sentEvent.getType());
        assertEquals("OldName", sentEvent.getProduct().getProductName());
        assertEquals(2, sentEvent.getProduct().getQuantity());
	}
	
	@Test
	@DisplayName("reduceProductQuantity should throw ProductQuantityNotEnoughException when amount exceeds stock")
	void reduceProductQuantity_InsufficientStock_ThrowsException() {
	    // Arrange
	    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

	    // Act
	    ProductQuantityNotEnoughException ex = assertThrows(
	        ProductQuantityNotEnoughException.class,
	        () -> productService.reduceProductQuantity(1L, 6) // request more than available
	    );

	    // Assert
	    assertEquals(
	        "Product amount exceed the product quantity available in our stock",
	        ex.getMessage()
	    );
	    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());

	    // Verify repository was only used to find the product
	    verify(productRepository).findById(1L);
	    verify(productRepository, never()).save(any());
	    verifyNoInteractions(template); // no Kafka event should be published
	}

}
