CREATE TABLE purchase_result(
    id BIGINT NOT NULL,
    purchase_id BIGINT NOT NULL,
    page TEXT NULL DEFAULT NULL,
    CONSTRAINT purchase_result_pkey PRIMARY KEY (id),
    CONSTRAINT purchase_result_purchase_id_fkey FOREIGN KEY (purchase_id) REFERENCES purchase (id)
)