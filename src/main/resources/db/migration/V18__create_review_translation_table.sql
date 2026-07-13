CREATE TABLE review_translation
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id          BIGINT       NOT NULL,
    language           VARCHAR(10)  NOT NULL,
    translated_content VARCHAR(600) NOT NULL,
    char_count         INT          NOT NULL,
    created_date       DATETIME(6)  NOT NULL,
    modified_date      DATETIME(6)  NOT NULL,
    CONSTRAINT uk_review_translation_review_lang UNIQUE (review_id, language),
    CONSTRAINT fk_review_translation_review
        FOREIGN KEY (review_id) REFERENCES review (review_id) ON DELETE CASCADE
);
