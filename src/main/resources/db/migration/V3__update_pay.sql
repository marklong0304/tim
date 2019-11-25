ALTER TABLE affiliate_commission DROP COLUMN IF EXISTS pay,
    ADD COLUMN paid timestamp without time zone;

ALTER TABLE salary_correction DROP COLUMN IF EXISTS pay,
    ADD COLUMN paid timestamp without time zone;