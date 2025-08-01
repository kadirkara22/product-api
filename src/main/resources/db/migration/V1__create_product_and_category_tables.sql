-- Create the product table
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    barcode VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create the category table
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create a foreign key constraint between products and categories
ALTER TABLE products
    ADD CONSTRAINT fk_category
        FOREIGN KEY (category_id)
            REFERENCES categories(id)