ALTER TABLE category
    ADD COLUMN created TIMESTAMP NULL DEFAULT NULL,
    ADD COLUMN modified TIMESTAMP NULL DEFAULT NULL;