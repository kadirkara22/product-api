
-- Insert default categories
INSERT INTO categories (id, name) VALUES
                                      (1, 'Electronics'),
                                      (2, 'Books'),
                                      (3, 'Clothing'),
                                      (4, 'Home Appliances'),
                                      (5, 'Sports & Outdoors'),
                                      (6, 'Health & Beauty'),
                                      (7, 'Toys & Games'),
                                      (8, 'Automotive'),
                                      (9, 'Grocery');

-- Insert 100 sample products manually category_id will be set to randomly chosen category
INSERT INTO products (id, name, barcode, sku, description, price, category_id) VALUES
                                                                                   (1, 'Smartphone', '1234567890123', 'SP-001', 'Latest model smartphone with advanced features.', 699.99, 1),
                                                                                   (2, 'Laptop', '1234567890124', 'LT-001', 'High-performance laptop for gaming and work.', 1299.99, 1),
                                                                                   (3, 'Novel Book', '1234567890125', 'BK-001', 'Bestselling novel of the year.', 19.99, 2),
                                                                                   (4, 'T-Shirt', '1234567890126', 'TS-001', 'Comfortable cotton t-shirt.', 15.99, 3),
                                                                                   (5, 'Microwave Oven', '1234567890127', 'MO-001', 'Compact microwave oven for quick meals.', 89.99, 4),
                                                                                   (6, 'Running Shoes', '1234567890128', 'RS-001', 'Lightweight running shoes for athletes.', 59.99, 5),
                                                                                   (7, 'Face Cream', '1234567890129', 'FC-001', 'Moisturizing face cream for daily use.', 29.99, 6),
                                                                                   (8, 'Action Figure', '1234567890130', 'AF-001', 'Collectible action figure from popular series.', 24.99, 7),
                                                                                   (9, 'Car Battery', '1234567890131', 'CB-001', 'High-performance car battery.', 119.99, 8),
                                                                                   (10, 'Organic Apples (1kg)', '1234567890132', 'GA-001', 'Fresh organic apples from local farms.', 3.99, 9);
