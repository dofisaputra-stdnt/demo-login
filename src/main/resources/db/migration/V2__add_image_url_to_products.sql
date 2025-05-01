-- V2__add_image_url_to_products.sql

ALTER TABLE products
    ADD COLUMN image_url VARCHAR(255);