-- ================================================
-- FOODYA DATABASE SEED DATA (PostgreSQL)
-- ================================================

-- Insert Restaurants
INSERT INTO restaurants (name, address, phone_number, description, cuisine, rating, total_reviews, is_open, is_active, opening_time, closing_time, delivery_fee, estimated_delivery_time, owner_id, created_at, updated_at)
VALUES
('The Italian Corner', '123 Nguyen Hue Boulevard, District 1, Ho Chi Minh City', '+84901234567', 'Authentic Italian cuisine', 'Italian', 4.5, 120, TRUE, TRUE, '09:00', '22:00', 2.5, 30, 1, NOW(), NOW()),
('Pho Saigon', '456 Le Loi Street, District 3, Ho Chi Minh City', '+84907654321', 'Traditional Vietnamese Pho', 'Vietnamese', 4.7, 200, TRUE, TRUE, '06:00', '23:00', 1.5, 25, 1, NOW(), NOW());

-- Insert Menu Items for Italian Restaurant (restaurant_id = 1)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(1, 'Margherita Pizza', 'Classic pizza with tomato, mozzarella, and basil', 12.99, 'Main Course', TRUE, TRUE, 15, 800, TRUE, FALSE, FALSE, FALSE, 45, NOW(), NOW()),
(1, 'Spaghetti Carbonara', 'Creamy pasta with bacon and parmesan', 14.99, 'Main Course', TRUE, TRUE, 20, 950, FALSE, FALSE, FALSE, FALSE, 67, NOW(), NOW()),
(1, 'Tiramisu', 'Traditional Italian dessert', 6.99, 'Dessert', TRUE, TRUE, 5, 450, TRUE, FALSE, FALSE, FALSE, 32, NOW(), NOW());

-- Insert Menu Items for Vietnamese Restaurant (restaurant_id = 2)
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(2, 'Pho Bo', 'Vietnamese beef noodle soup', 8.99, 'Main Course', TRUE, TRUE, 10, 600, FALSE, FALSE, TRUE, FALSE, 150, NOW(), NOW()),
(2, 'Banh Mi Thit', 'Vietnamese sandwich with pork', 4.99, 'Main Course', TRUE, TRUE, 8, 500, FALSE, FALSE, FALSE, FALSE, 89, NOW(), NOW()),
(2, 'Ca Phe Sua Da', 'Vietnamese iced coffee', 3.50, 'Beverage', TRUE, TRUE, 3, 150, TRUE, FALSE, TRUE, FALSE, 112, NOW(), NOW());
