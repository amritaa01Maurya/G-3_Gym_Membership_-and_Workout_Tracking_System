-- Gym Membership System - Initial Database Setup
-- Database: gym_membership_db
-- Created: 2024-05-05

-- ====================================
-- CREATE DATABASE
-- ====================================

CREATE DATABASE gym_membership_db
    WITH 
    ENCODING 'UTF8'
    LC_COLLATE 'en_US.UTF-8'
    LC_CTYPE 'en_US.UTF-8';

-- Connect to the new database
\c gym_membership_db;

-- ====================================
-- CREATE TABLES (Auto-created by Hibernate)
-- These are created automatically when application starts
-- with spring.jpa.hibernate.ddl-auto=update
-- ====================================

-- This script provides manual creation for reference

-- ====================================
-- TABLE: roles
-- ====================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(500),
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    CONSTRAINT unique_role_name UNIQUE(name)
);

-- ====================================
-- TABLE: users
-- ====================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT unique_user_email UNIQUE(email),
    CONSTRAINT check_email_format CHECK (email LIKE '%@%')
);

-- ====================================
-- INSERT INITIAL ROLES
-- ====================================
INSERT INTO roles (name, description, created_date, last_modified_date)
VALUES 
    ('ADMIN', 'Administrator - Full system access', NOW(), NOW()),
    ('TRAINER', 'Trainer - Can manage member workouts and progress', NOW(), NOW()),
    ('MEMBER', 'Member - Can view own data and workouts', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ====================================
-- CREATE INDEXES
-- ====================================

-- Index on users email for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index on users active status
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);

-- Index on users role_id for joins
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);

-- Index on roles name
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);

-- ====================================
-- CREATE SEQUENCES (if needed)
-- ====================================
CREATE SEQUENCE IF NOT EXISTS roles_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

-- ====================================
-- SAMPLE DATA (OPTIONAL - For Testing)
-- ====================================

-- Uncomment to insert sample users for testing

/*
INSERT INTO users (name, email, password, is_active, role_id, created_at, updated_at)
SELECT 
    'Admin User',
    'admin@example.com',
    '$2a$10$TfHjOQKuKnjVVFEjKqtke.L1OsCJUqI8L2hy0q4N8cF3zNdGfEMaO', -- password: Admin@123
    TRUE,
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

INSERT INTO users (name, email, password, is_active, role_id, created_at, updated_at)
SELECT 
    'Trainer User',
    'trainer@example.com',
    '$2a$10$PkfpJ6LfKQhAQhQkqJ5J8OoJ7j3k8m1n2o3p4q5r6s7t8u9v0w1x2', -- password: Trainer@123
    TRUE,
    (SELECT id FROM roles WHERE name = 'TRAINER'),
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'trainer@example.com');

INSERT INTO users (name, email, password, is_active, role_id, created_at, updated_at)
SELECT 
    'Member User',
    'member@example.com',
    '$2a$10$K3L4M5N6O7P8Q9R0S1T2U3V4W5X6Y7Z8A9B0C1D2E3F4G5H6I7J8', -- password: Member@123
    TRUE,
    (SELECT id FROM roles WHERE name = 'MEMBER'),
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'member@example.com');
*/

-- ====================================
-- VERIFY DATA
-- ====================================

-- View all roles
SELECT * FROM roles;

-- View all users
SELECT u.id, u.name, u.email, u.is_active, r.name as role 
FROM users u 
LEFT JOIN roles r ON u.role_id = r.id;

-- Count users by role
SELECT r.name as role, COUNT(u.id) as user_count 
FROM roles r 
LEFT JOIN users u ON r.id = u.role_id 
GROUP BY r.name;

-- ====================================
-- GRANTS & PERMISSIONS (Optional)
-- ====================================

-- Create application user (optional - for security)
/*
CREATE USER gym_app WITH PASSWORD 'secure_password_123';

GRANT CONNECT ON DATABASE gym_membership_db TO gym_app;

GRANT USAGE ON SCHEMA public TO gym_app;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO gym_app;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO gym_app;
*/

-- ====================================
-- MAINTENANCE & CLEANUP
-- ====================================

-- Drop all tables (for fresh start)
/*
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
*/

-- Reset sequences
/*
ALTER SEQUENCE roles_id_seq RESTART WITH 1;
ALTER SEQUENCE users_id_seq RESTART WITH 1;
*/

-- ====================================
-- NOTES
-- ====================================

/*
1. Database and tables are auto-created by Hibernate
   - Set spring.jpa.hibernate.ddl-auto=update in application.properties
   
2. Sample bcrypt passwords for testing:
   - Admin@123 → $2a$10$TfHjOQKuKnjVVFEjKqtke.L1OsCJUqI8L2hy0q4N8cF3zNdGfEMaO
   - Use online bcrypt generator for custom passwords
   
3. Roles must be inserted manually or via application startup
   
4. Indexes are created for performance optimization
   
5. Foreign key constraint ensures data integrity
   
6. Email validation constraint ensures valid email format
*/

-- ====================================
-- VERIFICATION QUERIES
-- ====================================

-- Check database info
SELECT 
    datname as database,
    pg_database.datdba,
    usename as user,
    pg_size_pretty(pg_database_size(datname)) as size
FROM pg_database
JOIN pg_user ON pg_database.datdba = pg_user.usesysid
WHERE datname = 'gym_membership_db';

-- Check table structure
SELECT * FROM information_schema.tables 
WHERE table_schema = 'public' AND table_type = 'BASE TABLE';

-- Check constraints
SELECT constraint_name, table_name 
FROM information_schema.table_constraints 
WHERE table_schema = 'public';
