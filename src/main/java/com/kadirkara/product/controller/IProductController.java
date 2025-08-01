package com.kadirkara.product.controller;
import com.kadirkara.product.dto.ProductRequest;
import com.kadirkara.product.dto.ProductResponse;
import com.kadirkara.product.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Product API", description = "API for managing products in the inventory")
public interface IProductController {
        @Operation(summary = "Get a product by ID", description = "Retrieve a product using its unique identifier")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Product found"),
                @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(value = "{\"error\": \"Product not found\"}")
                ))
        })
        @GetMapping("/{id}")
        EntityModel<ProductResponse> get(@PathVariable Long id);

        @Operation(summary = "List all products", description = "Retrieve a list of all products")
        @ApiResponse(responseCode = "200", description = "List of products retrieved successfully")
        @GetMapping
        CollectionModel<EntityModel<ProductResponse>> list(Pageable pageable);

        @Operation(summary = "Create a new product", description = "Add a new product to the inventory")
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Product created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(name = "Invalid name", value = "{\"error\": \"Product must not be empty\"}"),
                                @ExampleObject(name = "Invalid price", value = "{\"error\": \"Price must not be non-negative\"}")
                        }
                ))
        })
        @PostMapping
        ResponseEntity<EntityModel<ProductResponse>> create(@Valid @RequestBody ProductRequest productRequest);

        @Operation(summary = "Update product", description = "Update an existing product by ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Product updated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(name = "Invalid name", value = "{\"error\": \"Product must not be empty\"}"),
                                @ExampleObject(name = "Invalid price", value = "{\"error\": \"Price must not be non-negative\"}")
                        }
                )),
                @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(name = "Not found", value = "{\"error\": \"Product not found\"}"),
                        }
                ))
        })
        @PutMapping("/{id}")
        EntityModel<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest productRequest);

        @Operation(summary = "Delete a product", description = "Remove a product from the inventory by ID")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {
                                @ExampleObject(name = "Not found", value = "{\"error\": \"Product not found\"}"),
                        }
                ))
        })
        @DeleteMapping("/{id}")
        ResponseEntity<Void> delete(@PathVariable Long id);

}