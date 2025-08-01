package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create a new product"
    request {
        method POST()
        url "/api/products"
        headers {
            contentType("application/json")
            accept("application/hal+json")
        }
        body([
                name: "New Product",
                price: 149.99,
                description: "New Product Description",
                sku: "SKU002",
                barcode: "987654321",
                category: [
                        name: "Electronics"
                ]
        ])
    }
    response {
        status CREATED()
        headers {
            contentType("application/hal+json")
        }
        body([
                id: anyPositiveInt(),
                name: "Test Product",
                price: 99.99
        ])
    }
}