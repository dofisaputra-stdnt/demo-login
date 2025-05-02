-- Enable pgcrypto for UUID support
CREATE
    EXTENSION IF NOT EXISTS "pgcrypto";

-- Create table stores
CREATE TABLE stores
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name     VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL
);

-- Create table users
CREATE TABLE users
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username       VARCHAR(255) NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    login_attempts INT          NOT NULL DEFAULT 0,
    store_id       UUID         NULL,
    CONSTRAINT fk_store FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);

-- Create table users
CREATE TABLE customers
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username       VARCHAR(255) NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    login_attempts INT          NOT NULL DEFAULT 0,
    store_id       UUID         NOT NULL,
    CONSTRAINT fk_store FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);

-- Create table user_otps
CREATE TABLE user_otps
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL,
    otp             VARCHAR(255) NOT NULL,
    expiration_time TIMESTAMP    NOT NULL,
    is_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);