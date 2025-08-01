package com.kadirkara.product.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneOfSkuOrBarcodeRequiredValidator.class)
public @interface OneOfSkuOrBarcodeRequired {
    String message() default "Either SKU or Barcode must be provided";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
