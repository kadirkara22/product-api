package com.kadirkara.product.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;

    @Column(unique = true, nullable = false)
    private String barcode;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Product() {}

    public Product(Long id, String sku, String barcode, String name, String description, BigDecimal price, Category category) {
        this.id = id;
        this.sku = sku;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public static Product create(String sku, String barcode, String name, String description, BigDecimal price, Category category) {
        return new Product(null, sku, barcode, name, description, price, category);
    }

    public static Product from(Long id, String sku, String barcode, String name, String description, BigDecimal price, Category category) {
        return new Product(id, sku, barcode, name, description, price, category);
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}