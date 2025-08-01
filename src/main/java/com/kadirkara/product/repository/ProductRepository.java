package com.kadirkara.product.repository;

import com.kadirkara.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long>, PagingAndSortingRepository<Product,Long> {

}

