ALTER TABLE menu
    MODIFY COLUMN name_en VARCHAR(500) NULL;

UPDATE menu m
    JOIN (SELECT ranked.name_ko, ranked.restaurant, ranked.name_en
          FROM (SELECT mmm.name_ko,
                       ml.restaurant,
                       mmm.name_en,
                       ROW_NUMBER() OVER (
                           PARTITION BY mmm.name_ko, ml.restaurant
                           ORDER BY ml.date DESC, mmm.meal_main_menu_id DESC
                           ) AS rn
                FROM meal_main_menu mmm
                         JOIN meal ml ON ml.meal_id = mmm.meal_id
                WHERE TRIM(mmm.name_en) <> '') ranked
          WHERE ranked.rn = 1) latest
    ON CONVERT(latest.name_ko USING utf8mb4) COLLATE utf8mb4_bin = CONVERT(m.name USING utf8mb4) COLLATE utf8mb4_bin
        AND CAST(latest.restaurant AS CHAR) = CAST(m.restaurant AS CHAR)
SET m.name_en = latest.name_en
WHERE m.name_en IS NULL
   OR TRIM(m.name_en) = '';
