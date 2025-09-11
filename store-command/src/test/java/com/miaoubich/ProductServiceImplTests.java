package com.miaoubich;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
        		.productName("Moto-15")
        		.category("mobile")
        		.quantity(19)
        		.price(259.00)
        		.build();
        ReflectionTestUtils.setField(productService, "topic", "product-events");
    }
    
    @Test
    @DisplayName("addNewProduct should throw IllegalArgumentException when productRequest is null")
    void addNewProduct_NullRequest_ThrowsException() {
    	// Given
    	String errorMessage = "ProductRequest must not be null !";
    	
    	// When + Then
    	IllegalArgumentException illegalException = assertThrows(
    				IllegalArgumentException.class, () -> productService.addNewProduct(null)
    			);
    	assertEquals(errorMessage, illegalException.getMessage());
    	
    	// Verify that no repository or Kafka calls were made
    	verifyNoInteractions(productRepository, template, productMapper);
    }

    @Test
    @DisplayName("Product should successfully be created")
    void addProductSuccess() {
        // Given
        logger.info("productRequest -> {}", productRequest);

        Product mappedEntity = new Product("Moto-15", "Mobile", 19, 259.00);
        Product savedEntity = new Product("Moto-15", "Mobile", 19, 259.00);
        ReflectionTestUtils.setField(savedEntity, "productId", 1L);

        ProductResponse mappedResponse = new ProductResponse(1L, "Moto-15", "Mobile", 19, 259.00);

        when(productMapper.toEntity(productRequest)).thenReturn(mappedEntity);
        when(productRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(productMapper.toResponse(savedEntity)).thenReturn(mappedResponse);

        // When
        final ProductResponse result = productService.addNewProduct(productRequest);
        logger.info("result -> {}", result);

        // Then
        assertNotNull(result);
        assertEquals("Mobile", result.category());
        assertEquals(259.00, result.price());
        assertEquals("Moto-15", result.productName());
        assertEquals(19, result.quantity());

        // Capture and verify event
        String injectedTopic = (String) ReflectionTestUtils.getField(productService, "topic");
        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);

        verify(template).send(eq(injectedTopic), eventCaptor.capture());

        ProductEvent productEvent = eventCaptor.getValue();
        assertEquals("createProduct", productEvent.getType());
        assertEquals("Moto-15", productEvent.getProduct().getProductName());
    }
}

