package com.kadirkara.product.mapper;

import com.kadirkara.product.dto.ProductRequest;
import com.kadirkara.product.dto.ProductResponse;
import com.kadirkara.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    @Mapping(target = "price", expression = "java(new java.math.BigDecimal(productRequest.price()))")
    Product toEntity(ProductRequest productRequest);

}
