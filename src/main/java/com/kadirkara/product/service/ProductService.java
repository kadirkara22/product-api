package com.kadirkara.product.service;

import com.kadirkara.product.entity.Product;
import com.kadirkara.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        Product p = Product.create(
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory()
        );
        return productRepository.save(p);
    }

    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Cacheable(value = "products", key = "'page_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}