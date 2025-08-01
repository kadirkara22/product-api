package com.kadirkara.product.dto;


import com.kadirkara.product.validation.OneOfSkuOrBarcodeRequired;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@OneOfSkuOrBarcodeRequired(message = "Wrong SKU or Barcode")
public record ProductRequest(
        Long id,
        @NotBlank(message = "Name is required") String name,
        @Min(value = 0, message = "Price must be non-negative") double price,
        @Size(max = 255, message = "Description is too long") String description,
        String sku,
        String barcode,
        @NotNull(message="Category is required") @Valid CategoryRequest category
) {

}