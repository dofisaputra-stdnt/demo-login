-- Create table products
CREATE TABLE products
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255)     NOT NULL,
    price       DOUBLE PRECISION NOT NULL,
    description TEXT,
    image_url   VARCHAR(255)
);
