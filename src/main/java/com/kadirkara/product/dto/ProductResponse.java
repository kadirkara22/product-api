package com.kadirkara.product.dto;

public record ProductResponse(
        Long id,
        String name,
        double price
) {
}
