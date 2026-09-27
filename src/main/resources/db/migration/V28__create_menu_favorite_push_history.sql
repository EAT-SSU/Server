CREATE TABLE menu_favorite_push_history
(
    menu_favorite_push_history_id BIGINT      NOT NULL AUTO_INCREMENT,
    user_id                       BIGINT      NOT NULL,
    push_date                     DATE        NOT NULL,
    created_date                  DATETIME(6) NOT NULL,
    modified_date                 DATETIME(6) NOT NULL,
    PRIMARY KEY (menu_favorite_push_history_id),
    CONSTRAINT uk_menu_favorite_push_history_user_date UNIQUE (user_id, push_date),
    CONSTRAINT fk_menu_favorite_push_history_user FOREIGN KEY (user_id)
        REFERENCES user (user_id) ON DELETE CASCADE
);
