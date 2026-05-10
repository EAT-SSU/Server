-- =========================
-- V5: partnership 기간 타입 추가
-- =========================

ALTER TABLE partnership
    ADD COLUMN period_type ENUM ('NORMAL', 'FESTIVAL') NOT NULL DEFAULT 'NORMAL';
