package com.kadirkara.product.repository;

import com.kadirkara.product.entity.Category;
import com.kadirkara.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.swing.text.html.Option;
import java.util.Optional;


public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>, JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

}