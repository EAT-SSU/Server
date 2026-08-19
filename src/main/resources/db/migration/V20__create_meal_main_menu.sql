CREATE TABLE meal_main_menu (
    meal_main_menu_id BIGINT       NOT NULL AUTO_INCREMENT,
    meal_id           BIGINT       NOT NULL,
    seq               INT          NOT NULL,
    name_ko           VARCHAR(255) NOT NULL,
    name_en           VARCHAR(500) NOT NULL,
    created_date      DATETIME(6)  NOT NULL,
    modified_date     DATETIME(6)  NOT NULL,
    PRIMARY KEY (meal_main_menu_id),
    CONSTRAINT uk_meal_main_menu_seq UNIQUE (meal_id, seq),
    CONSTRAINT fk_meal_main_menu_meal FOREIGN KEY (meal_id)
        REFERENCES meal (meal_id) ON DELETE CASCADE
);
