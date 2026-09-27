CREATE TABLE push_token
(
    push_token_id BIGINT                  NOT NULL AUTO_INCREMENT,
    user_id       BIGINT                  NOT NULL,
    token         VARCHAR(512)            NOT NULL,
    device_type   ENUM ('IOS', 'ANDROID') NOT NULL,
    created_date  DATETIME(6)             NOT NULL,
    modified_date DATETIME(6)             NOT NULL,
    PRIMARY KEY (push_token_id),
    CONSTRAINT uk_push_token_token UNIQUE (token),
    CONSTRAINT fk_push_token_user FOREIGN KEY (user_id)
        REFERENCES user (user_id) ON DELETE CASCADE
);
