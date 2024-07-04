package com.programingish.product_service.service;

import com.programingish.product_service.dto.ProductRequest;
import com.programingish.product_service.dto.ProductResponse;
import com.programingish.product_service.model.Product;
import com.programingish.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
   private final ProductRepository productRepository;

    public void createProduct (ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescriptor())
                .build();
        productRepository.save(product);
        LOGGER.info("Product {} is saved", product.getId());
    }
    public List<ProductResponse> getAllProducts () {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
