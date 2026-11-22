ALTER TABLE menu ADD COLUMN name_key VARCHAR(255) NULL;

CREATE INDEX idx_menu_name_key_restaurant ON menu (name_key, restaurant);
