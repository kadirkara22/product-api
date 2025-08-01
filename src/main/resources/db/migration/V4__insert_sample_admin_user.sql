-- Insert sample admin user
-- Password is 'admin123' encoded with BCrypt (strength 10)
INSERT INTO users (username, password) VALUES
    ('admin', '$2a$10$8LSbss3c7CPtQ1UkeNdgiequMYHO55qp9SBd28MX6gZxvSGSar33a');

-- Insert admin role for the user
INSERT INTO user_roles (user_id, role) VALUES
                                           ((SELECT id FROM users WHERE username = 'admin'), 'ROLE_ADMIN'),
                                           ((SELECT id FROM users WHERE username = 'admin'), 'ROLE_USER');