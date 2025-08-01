
package com.kadirkara.product.controller;

import com.kadirkara.product.dto.ProductRequest;
import com.kadirkara.product.dto.ProductResponse;
import com.kadirkara.product.entity.Product;
import com.kadirkara.product.mapper.ProductMapper;
import com.kadirkara.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
@RequestMapping("/api/products")
public class ProductController implements IProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Override
    public EntityModel<ProductResponse> get(@PathVariable Long id) {
        ProductResponse product = productMapper.toResponse(productService.findById(id));
        if (product == null) {
            throw new NoSuchElementException("Product not found");
        }
        return toModel(product);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public CollectionModel<EntityModel<ProductResponse>> list(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        List<EntityModel<ProductResponse>> productsResponse = productService.findAll(pageable).stream()
                .map(productMapper::toResponse)
                .map(this::toModel)
                .toList();
        return CollectionModel.of(productsResponse,
                linkTo(methodOn(ProductController.class).list(pageable)).withSelfRel());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<ProductResponse>> create(@Valid @RequestBody ProductRequest productRequest) {
        Product p = productMapper.toEntity(productRequest);
        Product save = productService.save(p);
        ProductResponse product = productMapper.toResponse(save);
        EntityModel<ProductResponse> entityModel = toModel(product);
        return ResponseEntity.created(linkTo(methodOn(ProductController.class).get(product.id())).toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        ProductRequest updatedProductRequest = new ProductRequest(
                id,
                productRequest.name(),
                productRequest.price(),
                productRequest.description(),
                productRequest.sku(),
                productRequest.barcode(),
                productRequest.category());
        Product p = productService.update(productMapper.toEntity(updatedProductRequest));
        ProductResponse newProduct = productMapper.toResponse(p);
        return toModel(newProduct);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ProductResponse> toModel(ProductResponse product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).get(product.id())).withSelfRel(),
                linkTo(methodOn(ProductController.class).list(null)).withRel("products"));
    }
}