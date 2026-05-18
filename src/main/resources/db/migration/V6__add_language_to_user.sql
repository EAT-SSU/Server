ALTER TABLE user
    ADD COLUMN language ENUM ('KO', 'EN', 'JA', 'VI') NOT NULL DEFAULT 'KO';
