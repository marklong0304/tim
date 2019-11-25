CREATE TABLE affiliate_payment (
  id BIGINT NOT NULL,
  payment_date TIMESTAMP NULL DEFAULT NULL,
  affiliate_user_id BIGINT NULL DEFAULT NULL,
  affiliate_company_id BIGINT NULL DEFAULT NULL,
  payment_option VARCHAR(255) NULL DEFAULT NULL,
  bank_name VARCHAR(255) NULL DEFAULT NULL,
  bank_routing VARCHAR(255) NULL DEFAULT NULL,
  account VARCHAR(255) NULL DEFAULT NULL,
  paypal_email_address VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY ("id")
);

ALTER TABLE affiliate_commission ADD COLUMN affiliate_payment_id BIGINT NULL DEFAULT NULL;
ALTER TABLE affiliate_commission ADD FOREIGN KEY (affiliate_payment_id) REFERENCES affiliate_payment(id);
ALTER TABLE affiliate_commission ADD COLUMN salary_paid NUMERIC(19,2) NULL DEFAULT NULL;

ALTER TABLE salary_correction ADD COLUMN affiliate_payment_id BIGINT NULL DEFAULT NULL;
ALTER TABLE salary_correction ADD FOREIGN KEY (affiliate_payment_id) REFERENCES affiliate_payment(id);
ALTER TABLE salary_correction ADD COLUMN salary_paid NUMERIC(19,2) NULL DEFAULT NULL;