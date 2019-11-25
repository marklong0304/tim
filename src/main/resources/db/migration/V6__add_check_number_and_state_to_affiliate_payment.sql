ALTER TABLE affiliate_payment
    ADD COLUMN check_number VARCHAR(255),
    ADD COLUMN status_paid TIMESTAMP NULL DEFAULT NULL;