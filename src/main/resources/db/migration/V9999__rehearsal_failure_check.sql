ALTER TABLE college
ADD CONSTRAINT chk_rehearsal_college_id_over_100
CHECK (college_id > 100);
