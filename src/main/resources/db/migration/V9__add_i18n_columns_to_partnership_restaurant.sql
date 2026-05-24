ALTER TABLE partnership_restaurant
    RENAME COLUMN store_name TO store_name_ko,
    ADD COLUMN store_name_en VARCHAR(255) NULL,
    ADD COLUMN store_name_ja VARCHAR(255) NULL,
    ADD COLUMN store_name_vi VARCHAR(255) NULL;
