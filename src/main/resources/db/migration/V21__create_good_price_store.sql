CREATE TABLE good_price_store (
    id BIGINT NOT NULL AUTO_INCREMENT,
    source_id INT NOT NULL,
    category VARCHAR(20) NOT NULL,
    store_name VARCHAR(100) NOT NULL,
    main_menu VARCHAR(100),
    price INT,
    road_address VARCHAR(255) NOT NULL,
    district VARCHAR(20) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    image_url_1 VARCHAR(2048),
    image_url_2 VARCHAR(2048),
    image_url_3 VARCHAR(2048),
    PRIMARY KEY (id)
);

CREATE INDEX idx_good_price_store_category ON good_price_store (category);
CREATE INDEX idx_good_price_store_source_id ON good_price_store (source_id);
