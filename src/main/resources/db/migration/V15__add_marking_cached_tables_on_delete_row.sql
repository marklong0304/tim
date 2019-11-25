CREATE TABLE service_param (
    id SERIAL PRIMARY KEY,
    code TEXT UNIQUE,
    cached_tables_changed BOOLEAN
);

CREATE OR REPLACE FUNCTION mark_tables_changed() RETURNS trigger AS
$$
BEGIN
    IF (SELECT COUNT(*) FROM service_param) > 0 THEN
        UPDATE service_param SET cached_tables_changed = TRUE WHERE code = 'cachedTablesChanged';
    ELSE
        INSERT INTO service_param(code, cached_tables_changed) VALUES ('cachedTablesChanged', TRUE);
    END IF;
    RETURN OLD;
END;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER mark_policy_meta_changed BEFORE DELETE ON policy_meta EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_page_changed BEFORE DELETE ON policy_meta_page EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_subcategory_value_changed BEFORE DELETE ON subcategory_value EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_restriction_changed BEFORE DELETE ON policy_meta_restriction EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_category_changed BEFORE DELETE ON policy_meta_category EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_category_value_changed BEFORE DELETE ON policy_meta_category_value EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_category_restriction_changed BEFORE DELETE ON policy_meta_category_restriction EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_category_content_changed BEFORE DELETE ON policy_meta_category_content EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_package_changed BEFORE DELETE ON policy_meta_package EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_package_value_changed BEFORE DELETE ON policy_meta_package_value EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_package_restriction_changed BEFORE DELETE ON policy_meta_package_restriction EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_quote_param_changed BEFORE DELETE ON policy_quote_param EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_quote_param_restriction_changed BEFORE DELETE ON policy_quote_param_restriction EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_code_changed BEFORE DELETE ON policy_meta_code EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_policy_meta_code_restriction_changed BEFORE DELETE ON policy_meta_code_restriction EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_vendor_changed BEFORE DELETE ON vendor EXECUTE PROCEDURE mark_tables_changed();
CREATE TRIGGER mark_category_changed BEFORE DELETE ON category EXECUTE PROCEDURE mark_tables_changed();