package com.programingish.product_service.productservice;

import com.programingish.product_service.dto.ProductRequest;
import com.programingish.product_service.dto.ProductResponse;
import com.programingish.product_service.model.Product;
import com.programingish.product_service.repository.ProductRepository;
import com.programingish.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductService productService;

	private Product product;
	private ProductRequest productRequest;

	@BeforeEach
	void setUp() {
		product = Product.builder()
				.id(1L)
				.name("Test Product")
				.description("Test Description")
				.price(new BigDecimal("9.99"))
				.build();

		productRequest = ProductRequest.builder()
				.name("Test Product")
				.descriptor("Test Description")
				.price(new BigDecimal("9.99"))
				.build();
	}

	@Test
	void testCreateProduct() {
		productService.createProduct(productRequest);

		ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
		verify(productRepository).save(productArgumentCaptor.capture());
		Product capturedProduct = productArgumentCaptor.getValue();

		assertEquals(productRequest.getName(), capturedProduct.getName());
		assertEquals(productRequest.getDescriptor(), capturedProduct.getDescription());
		assertEquals(productRequest.getPrice(), capturedProduct.getPrice());
	}

	@Test
	void testGetAllProducts() {
		when(productRepository.findAll()).thenReturn(List.of(product));

		List<ProductResponse> products = productService.getAllProducts();

		assertEquals(1, products.size());
		ProductResponse productResponse = products.get(0);
		assertEquals(product.getId(), productResponse.getId());
		assertEquals(product.getName(), productResponse.getName());
		assertEquals(product.getDescription(), productResponse.getDescription());
		assertEquals(product.getPrice(), productResponse.getPrice());
	}
}
