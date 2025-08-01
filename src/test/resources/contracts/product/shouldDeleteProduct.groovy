package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should delete a product"
    request {
        method DELETE()
        url "/api/products/1"
    }
    response {
        status NO_CONTENT()
    }
}