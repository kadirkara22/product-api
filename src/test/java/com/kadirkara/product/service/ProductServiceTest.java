package com.kadirkara.product.service;


import com.kadirkara.product.entity.Category;
import com.kadirkara.product.entity.Product;
import com.kadirkara.product.repository.CategoryRepository;
import com.kadirkara.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setSku("SKU001");
        testProduct.setBarcode("123456789");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setCategory(testCategory);
    }

    @Test
    @DisplayName("Should save product successfully when category exists")
    void shouldSaveProductSuccessfully() {
        // Given
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product savedProduct = productService.save(testProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getCategory().getName()).isEqualTo("Electronics");
        verify(categoryRepository).findByName("Electronics");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when category does not exist")
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.save(testProduct))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category not found: Electronics");

        verify(categoryRepository).findByName("Electronics");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should find product by id successfully")
    void shouldFindProductByIdSuccessfully() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Product foundProduct = productService.findById(productId);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(productId);
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product not found by id")
    void shouldThrowExceptionWhenProductNotFoundById() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.findById(productId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should delete product by id successfully")
    void shouldDeleteProductByIdSuccessfully() {
        // Given
        Long productId = 1L;

        // When
        productService.deleteById(productId);

        // Then
        verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        Product updatedProduct = productService.update(testProduct);

        // Then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(testProduct.getId());
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should find all products with pagination")
    void shouldFindAllProductsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> result = productService.findAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(productRepository).findAll(pageable);
    }
}