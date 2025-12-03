-- ================================================
-- FOODYA DATABASE SEED DATA
-- ================================================

-- Insert Restaurants
INSERT INTO restaurants (name, address, phone_number, description, cuisine, rating, total_reviews, is_open, is_active, opening_time, closing_time, delivery_fee, estimated_delivery_time, owner_id, created_at, updated_at)
VALUES
('The Italian Corner', '123 Nguyen Hue Boulevard, District 1, Ho Chi Minh City', '+84901234567', 'Authentic Italian cuisine', 'Italian', 4.5, 120, 1, 1, '09:00', '22:00', 2.5, 30, 1, NOW(), NOW()),
('Pho Saigon', '456 Le Loi Street, District 3, Ho Chi Minh City', '+84907654321', 'Traditional Vietnamese Pho', 'Vietnamese', 4.7, 200, 1, 1, '06:00', '23:00', 1.5, 25, 1, NOW(), NOW());

-- Insert Menu Items for Italian Restaurant
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(1, 'Margherita Pizza', 'Classic pizza with tomato, mozzarella, and basil', 12.99, 'Main Course', 1, 1, 15, 800, 1, 0, 0, 0, 45, NOW(), NOW()),
(1, 'Spaghetti Carbonara', 'Creamy pasta with bacon and parmesan', 14.99, 'Main Course', 1, 1, 20, 950, 0, 0, 0, 0, 67, NOW(), NOW()),
(1, 'Tiramisu', 'Traditional Italian dessert', 6.99, 'Dessert', 1, 1, 5, 450, 1, 0, 0, 0, 32, NOW(), NOW());

-- Insert Menu Items for Vietnamese Restaurant
INSERT INTO menu_items (restaurant_id, name, description, price, category, is_available, is_active, preparation_time, calories, is_vegetarian, is_vegan, is_gluten_free, is_spicy, order_count, created_at, updated_at)
VALUES
(2, 'Pho Bo', 'Vietnamese beef noodle soup', 8.99, 'Main Course', 1, 1, 10, 600, 0, 0, 1, 0, 150, NOW(), NOW()),
(2, 'Banh Mi Thit', 'Vietnamese sandwich with pork', 4.99, 'Main Course', 1, 1, 8, 500, 0, 0, 0, 0, 89, NOW(), NOW()),
(2, 'Ca Phe Sua Da', 'Vietnamese iced coffee', 3.50, 'Beverage', 1, 1, 3, 150, 1, 0, 1, 0, 112, NOW(), NOW());
