package com.kadirkara.product.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response model for API errors")
public record ErrorResponse(
     @Schema(description = "Error Message",example="Product not found")
     String error
) {
}
