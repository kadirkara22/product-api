package com.kadirkara.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kadirkara.product", "com.kadirkara.auth"})
@EnableJpaRepositories(basePackages = {"com.kadirkara.product.repository", "com.kadirkara.auth.repository"})
@EntityScan(basePackages = {"com.kadirkara.product.entity", "com.kadirkara.auth.entity"})
public class ProductApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiApplication.class, args);
	}

}
