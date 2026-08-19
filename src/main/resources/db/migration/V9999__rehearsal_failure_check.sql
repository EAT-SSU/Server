INSERT INTO review (review_id, created_date, modified_date)
SELECT existing_review.review_id, NOW(6), NOW(6)
FROM (
    SELECT review_id
    FROM review
    LIMIT 1
) existing_review;
