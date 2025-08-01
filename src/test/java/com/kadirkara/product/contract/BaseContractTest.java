package com.kadirkara.product.contract;

import com.kadirkara.product.controller.ProductController;
import com.kadirkara.product.dto.ProductResponse;
import com.kadirkara.product.entity.Category;
import com.kadirkara.product.entity.Product;
import com.kadirkara.product.exception.GlobalExceptionHandler;
import com.kadirkara.product.mapper.ProductMapper;
import com.kadirkara.product.service.ProductService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public abstract class BaseContractTest {

    @Mock
    ProductService productService;

    @Mock
    ProductMapper productMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create controller with mocked dependencies
        ProductController productController = new ProductController(productService, productMapper);

        // Setup standalone MockMvc with exception handler and custom argument resolvers
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        RestAssuredMockMvc.mockMvc(mockMvc);
        setupMockData();
    }

    private void setupMockData() {
        // Setup test data
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU001");
        product.setBarcode("123456789");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCategory(category);

        // Create ProductResponse with correct structure (only 3 fields)
        ProductResponse productResponse = new ProductResponse(
                1L,
                "Test Product",
                99.99  // Convert BigDecimal to double for the DTO
        );

        // Mock service behavior for GET /api/products/1
        given(productService.findById(1L)).willReturn(product);
        given(productMapper.toResponse(product)).willReturn(productResponse);

        // Mock service behavior for GET /api/products/999 (throw NoSuchElementException for proper 404 handling)
        given(productService.findById(999L)).willThrow(new java.util.NoSuchElementException("Product not found"));

        // Mock for create product (POST /api/products)
        given(productService.save(any(Product.class))).willReturn(product);
        given(productMapper.toEntity(any())).willReturn(product);
        given(productMapper.toResponse(any(Product.class))).willReturn(productResponse);

        // Mock for list products (GET /api/products) - simplified without Pageable
        given(productService.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(product)));

        // Mock for update product (PUT /api/products/1)
        given(productService.update(any(Product.class))).willReturn(product);

        // Mock for delete product (DELETE /api/products/1) - no return value needed
    }
}
