package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should update an existing product"
    request {
        method PUT()
        url "/api/products/1"
        headers {
            contentType("application/json")
            accept("application/hal+json")
        }
        body([
                id: 1,
                name: "Updated Product",
                price: 199.99,
                description: "Updated Description",
                sku: "SKU001",
                barcode: "123456789",
                category: [
                        name: "Electronics"
                ]
        ])
    }
    response {
        status OK()
        headers {
            contentType("application/hal+json")
        }
        body([
                id: 1,
                name: "Test Product",
                price: 99.99
        ])
    }
}