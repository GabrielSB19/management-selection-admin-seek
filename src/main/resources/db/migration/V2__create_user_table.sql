-- Migration V2: Create simple users table for authentication
-- Description: Simple users table with basic fields for JWT authentication

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    
    PRIMARY KEY (id)
);

-- Create indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Insert default users with BCrypt hashed passwords (strength 12)
-- admin: password = admin123
-- user1: password = user123
INSERT INTO users (username, email, password, first_name, last_name, role, enabled) VALUES 
('admin', 'admin@seek.com', '$2a$12$LQv3c1yqBWVHxkd0LQ1lqe4.rCuwCvFKw3YQFU2vDz6CCz6bLOu/6', 'System', 'Administrator', 'ADMIN', TRUE),
('user1', 'user1@seek.com', '$2a$12$vQJMLhX8OKMSsKbqRbJdMeUcNvyNvl1VFOe4bDcLVzwQVhCZBJ7nS', 'John', 'Doe', 'USER', TRUE);
