CREATE TABLE notification
(
    notification_id BIGINT                   NOT NULL AUTO_INCREMENT,
    user_id         BIGINT                   NOT NULL,
    type            ENUM ('MENU_FAVORITE')   NOT NULL,
    target_date     DATE                     NOT NULL,
    is_read         BIT                      NOT NULL DEFAULT b'0',
    created_date    DATETIME(6)              NOT NULL,
    modified_date   DATETIME(6)              NOT NULL,
    PRIMARY KEY (notification_id),
    CONSTRAINT uk_notification_user_type_date UNIQUE (user_id, type, target_date),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id)
        REFERENCES user (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_target_date ON notification (target_date);

CREATE TABLE notification_menu
(
    notification_menu_id BIGINT                                                                  NOT NULL AUTO_INCREMENT,
    notification_id      BIGINT                                                                  NOT NULL,
    menu_name            VARCHAR(255)                                                            NOT NULL,
    menu_name_en         VARCHAR(255)                                                            NULL,
    restaurant           ENUM ('DODAM', 'DORMITORY', 'FOOD_COURT', 'SNACK_CORNER', 'HAKSIK', 'FACULTY') NOT NULL,
    time_part            ENUM ('MORNING', 'LUNCH', 'DINNER')                                     NOT NULL,
    PRIMARY KEY (notification_menu_id),
    CONSTRAINT fk_notification_menu_notification FOREIGN KEY (notification_id)
        REFERENCES notification (notification_id) ON DELETE CASCADE
);
