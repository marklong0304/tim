ALTER TABLE purchase RENAME cancel TO cancelled;
ALTER TABLE purchase ALTER COLUMN cancelled DROP NOT NULL;
ALTER TABLE purchase ALTER COLUMN cancelled DROP DEFAULT;
ALTER TABLE purchase ALTER COLUMN cancelled TYPE TIMESTAMP USING CASE WHEN cancelled THEN NOW() ELSE NULL END;

ALTER TABLE purchase RENAME deleted_by TO cancelled_by;

ALTER TABLE purchase DROP COLUMN deleted;
