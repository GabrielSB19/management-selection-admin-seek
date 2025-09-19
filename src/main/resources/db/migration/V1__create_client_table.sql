-- Migration V1: Create clients table
-- Description: Main table to store candidate/client information

CREATE TABLE clients (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT 'Client first name',
    last_name VARCHAR(100) NOT NULL COMMENT 'Client last name',
    age INT NOT NULL COMMENT 'Current age of the client',
    birth_date DATE NOT NULL COMMENT 'Date of birth',
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Registration date in the system',
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    
    PRIMARY KEY (id),
    
    -- Validation constraints (without dynamic functions for MySQL 9.4 compatibility)
    CONSTRAINT chk_age_valid CHECK (age >= 18 AND age <= 120)
);

-- Indexes to optimize metrics queries
CREATE INDEX idx_clients_age ON clients(age);
CREATE INDEX idx_clients_birth_date ON clients(birth_date);
CREATE INDEX idx_clients_name_last_name ON clients(name, last_name);

-- Sample data for initial testing
INSERT INTO clients (name, last_name, age, birth_date) VALUES
('Juan', 'Pérez', 30, '1993-05-15'),
('María', 'González', 25, '1998-08-22'),
('Carlos', 'López', 35, '1988-12-10'),
('Ana', 'Martínez', 28, '1995-03-07'),
('Luis', 'Rodríguez', 42, '1981-11-18');

-- Verify that data was inserted correctly
SELECT COUNT(*) as total_clients FROM clients;
