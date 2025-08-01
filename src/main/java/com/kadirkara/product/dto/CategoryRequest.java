package com.kadirkara.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(

        @NotBlank(message = "Category is required") String name
) {
}
