CREATE TABLE menu_favorite
(
    menu_favorite_id BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    menu_id          BIGINT      NOT NULL,
    created_date     DATETIME(6) NOT NULL,
    modified_date    DATETIME(6) NOT NULL,
    PRIMARY KEY (menu_favorite_id),
    CONSTRAINT uk_menu_favorite_user_menu UNIQUE (user_id, menu_id),
    CONSTRAINT fk_menu_favorite_user FOREIGN KEY (user_id)
        REFERENCES user (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_favorite_menu FOREIGN KEY (menu_id)
        REFERENCES menu (menu_id) ON DELETE CASCADE
);
