package com.kadirkara.product.validation;

import com.kadirkara.product.dto.ProductRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneOfSkuOrBarcodeRequiredValidator implements ConstraintValidator<OneOfSkuOrBarcodeRequired, ProductRequest> {


    @Override
    public boolean isValid(ProductRequest productRequest, ConstraintValidatorContext constraintValidatorContext) {
        if(productRequest==null) return true;

        return (productRequest.sku()!=null && !productRequest.sku().isBlank()) ||
                (productRequest.barcode()!=null && !productRequest.barcode().isBlank());
    }
}
