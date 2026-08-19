ALTER TABLE review
ADD CONSTRAINT chk_rehearsal_review_id_over_1000000000
CHECK (review_id > 1000000000);
