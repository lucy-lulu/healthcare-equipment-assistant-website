USE novis;

-- Insert Users: all the original password is 'Password1!', stored the hashed version
-- Partner user
SET @partner_id = UUID();
INSERT INTO users (id, username, email, password_hash, role)
VALUES (@partner_id, 'partner_jane', 'jane@partner.com', '$2b$10$Q3oprmUM/jMdj/ncYGYYKefl4rMKRQUUPjSG2B6lJjZjAqzDGei.2', 'partner');

-- Sales user
SET @sales_id = UUID();
INSERT INTO users (id, username, email, password_hash, role)
VALUES (@sales_id, 'sales_john', 'john@novis.com', '$2b$10$Q3oprmUM/jMdj/ncYGYYKefl4rMKRQUUPjSG2B6lJjZjAqzDGei.2', 'sales');

-- OT user(not used in this stage)
SET @ot_id = UUID();
INSERT INTO users (id, username, email, password_hash, role)
VALUES (@ot_id, 'ot_emily', 'emily@ot.org', '$2b$10$Q3oprmUM/jMdj/ncYGYYKefl4rMKRQUUPjSG2B6lJjZjAqzDGei.2', 'ot');

-- Admin user(not used in this stage)
SET @admin_id = UUID();
INSERT INTO users (id, username, email, password_hash, role)
VALUES (@admin_id, 'admin_steve', 'steve@novis.com', '$2b$10$Q3oprmUM/jMdj/ncYGYYKefl4rMKRQUUPjSG2B6lJjZjAqzDGei.2', 'admin');

-- Insert Categories: main category(1,2), sub category(3,4)
INSERT INTO categories (name, parent_id) VALUES
('Mobility Aids', NULL),        -- id = 1
('Foam Supports', NULL),        -- id = 2
('Wheelchairs', 1),             -- id = 3 (child of id 1)
('Seat Cushions', 2);           -- id = 4 (child of id 2)

-- Insert Products
-- Product 1: have extra attributes
INSERT INTO products (
    name, sku, size, description, category_id, image_url, features,
    height, width, length, thickness, weight, depth,
    material, warranty_years, compliance_artg_number, au_standard_number,
    price, has_extra_attributes
) VALUES (
    'Comfort Foam Cushion',
    'SKU-CFC001',
    'Standard',
    'A medical-grade foam cushion for wheelchairs.',
    4,
    'https://example.com/image1.jpg',
    'Foldable, high-density, washable cover',
    335, 670, 1260, 25, 3000, 100,
    'Medical-grade foam',
    2, '278802', 'AS12345',
    179.99,
    TRUE
);

-- Product 2: without extra attributes
INSERT INTO products (
    name, sku, size, description, category_id, image_url, features,
    height, width, length, thickness, weight, depth,
    material, warranty_years, compliance_artg_number, au_standard_number,
    price, has_extra_attributes
) VALUES (
    'Lightweight Wheelchair',
    'SKU-LWC002',
    'Large',
    'Aluminium-frame wheelchair with adjustable height.',
    3,
    'https://example.com/image2.jpg',
    'Foldable frame, push handles, detachable footrests',
    950, 600, 1000, 0, 8500, 200,
    'Aluminium',
    3, '298301', 'AS45678',
    1249.50,
    FALSE
);

-- Insert extra Product Attributes (for product_id = 1)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value)
VALUES
(1, 'Color Options', 'Blue, Grey'),
(1, 'Washable Cover', 'Yes'),
(1, 'Packaging', 'Sold in pairs');

-- Insert Quote (linked to generated users)
INSERT INTO quotes (creator_id, responder_id, status, total_price)
VALUES (@partner_id, @sales_id, 'responded', 1429.49);

-- Insert Quote Items
INSERT INTO quote_items (quote_id, product_id, quantity)
VALUES
(1, 1, 2),  -- 2 Comfort Foam Cushion
(1, 2, 1);  -- 1 Lightweight Wheelchair
