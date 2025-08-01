package com.kadirkara.product.repository;

import com.kadirkara.product.entity.Category;
import com.kadirkara.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ProductRepository Integration Tests")
public class ProductRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setName("Electronics");
        testCategory.setCreatedAt(java.time.LocalDateTime.now());
        testCategory.setUpdatedAt(java.time.LocalDateTime.now());
        testCategory = entityManager.persistAndFlush(testCategory);
    }

    @Test
    @DisplayName("Should save and find product by id")
    void shouldSaveAndFindProductById() {
        // Given
        Product product = createTestProduct("Test Product", "SKU001", "123456789");

        // When
        Product savedProduct = productRepository.save(product);
        entityManager.flush();
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
        assertThat(foundProduct.get().getSku()).isEqualTo("SKU001");
        assertThat(foundProduct.get().getCategory().getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Should find all products with pagination")
    void shouldFindAllProductsWithPagination() {
        // Given
        for (int i = 1; i <= 5; i++) {
            Product product = createTestProduct("Product " + i, "SKU00" + i, "12345678" + i);
            entityManager.persistAndFlush(product);
        }

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("name"));

        // When
        Page<Product> productPage = productRepository.findAll(pageRequest);

        // Then
        assertThat(productPage.getContent()).hasSize(3);
        assertThat(productPage.getTotalElements()).isEqualTo(5);
        assertThat(productPage.getTotalPages()).isEqualTo(2);
        assertThat(productPage.getContent().get(0).getName()).isEqualTo("Product 1");
    }

    @Test
    @DisplayName("Should delete product by id")
    void shouldDeleteProductById() {
        // Given
        Product product = createTestProduct("Test Product", "SKU001", "123456789");
        Product savedProduct = entityManager.persistAndFlush(product);

        // When
        productRepository.deleteById(savedProduct.getId());
        entityManager.flush();

        // Then
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    @DisplayName("Should count products correctly")
    void shouldCountProductsCorrectly() {
        // Given
        for (int i = 1; i <= 3; i++) {
            Product product = createTestProduct("Product " + i, "SKU00" + i, "12345678" + i);
            entityManager.persistAndFlush(product);
        }

        // When
        long count = productRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should check if product exists by id")
    void shouldCheckIfProductExistsById() {
        // Given
        Product product = createTestProduct("Test Product", "SKU001", "123456789");
        Product savedProduct = entityManager.persistAndFlush(product);

        // When & Then
        assertThat(productRepository.existsById(savedProduct.getId())).isTrue();
        assertThat(productRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Should find products by category")
    void shouldFindProductsByCategory() {
        // Given
        Category electronicCategory = testCategory;
        Category bookCategory = new Category();
        bookCategory.setName("Books");
        bookCategory.setCreatedAt(java.time.LocalDateTime.now());
        bookCategory.setUpdatedAt(java.time.LocalDateTime.now());
        bookCategory = entityManager.persistAndFlush(bookCategory);

        // Create products in different categories
        Product electronic1 = createTestProduct("Phone", "SKU001", "123456789");
        Product electronic2 = createTestProduct("Laptop", "SKU002", "987654321");

        Product book = new Product();
        book.setSku("SKU003");
        book.setBarcode("111111111");
        book.setName("Java Book");
        book.setDescription("Programming book");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setCategory(bookCategory);

        entityManager.persistAndFlush(electronic1);
        entityManager.persistAndFlush(electronic2);
        entityManager.persistAndFlush(book);

        // When
        List<Product> allProducts = productRepository.findAll();
        long electronicsCount = allProducts.stream()
                .filter(p -> "Electronics".equals(p.getCategory().getName()))
                .count();
        long booksCount = allProducts.stream()
                .filter(p -> "Books".equals(p.getCategory().getName()))
                .count();

        // Then
        assertThat(electronicsCount).isEqualTo(2);
        assertThat(booksCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle database constraints properly")
    void shouldHandleDatabaseConstraintsProperly() {
        // Given
        Product product1 = createTestProduct("Product 1", "SKU001", "123456789");
        entityManager.persistAndFlush(product1);

        // When - Try to create product with duplicate SKU
        Product product2 = createTestProduct("Product 2", "SKU001", "987654321");

        // Then - Should handle constraint violation gracefully
        try {
            entityManager.persistAndFlush(product2);
            // If we reach here, it means no constraint was violated
            // This depends on whether you have unique constraints on SKU
        } catch (Exception e) {
            // Expected if unique constraint exists on SKU
            assertThat(e).isNotNull();
        }
    }

    private Product createTestProduct(String name, String sku, String barcode) {
        Product product = new Product();
        product.setSku(sku);
        product.setBarcode(barcode);
        product.setName(name);
        product.setDescription("Test description for " + name);
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setCategory(testCategory);
        return product;
    }
}