package contracts.product

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return list of products"
    request {
        method GET()
        url "/api/products"
        headers {
            accept("application/hal+json")
        }
    }
    response {
        status OK()
        headers {
            contentType("application/hal+json")
        }
        body([
                [
                        id: 1,
                        name: "Test Product",
                        price: 99.99
                ]
        ])
    }
}