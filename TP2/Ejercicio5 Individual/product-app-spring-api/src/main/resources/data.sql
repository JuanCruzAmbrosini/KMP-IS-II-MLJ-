INSERT INTO category (id, title, slug, meta_title) VALUES
(1, 'Home and Office', 'home-office', 'Home and Office Products'),
(2, 'Electronics', 'electronics', 'Electronic Products');

INSERT INTO sub_category (id, description, categoryid) VALUES
(1, 'Home Comfort', 1),
(2, 'Lighting', 2),
(3, 'Gaming Accessories', 2);

INSERT INTO product (title, summary, created_at, content, categoryid, subcategoryid) VALUES
('UltraSoft Pillow', 'A comfortable pillow for a good night''s sleep.',
 CURRENT_TIMESTAMP, 'Soft and durable pillow.', 1, 1),
('EcoSmart LED Bulb', 'Energy-saving LED bulb.',
 CURRENT_TIMESTAMP, 'LED bulb with a long service life.', 2, 2),
('ProGaming Mouse', 'Ergonomic mouse with customizable RGB lighting.',
 CURRENT_TIMESTAMP, 'High-precision gaming mouse.', 2, 3);
