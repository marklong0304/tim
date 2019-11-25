CREATE TABLE affiliate_commission (
    id bigint NOT NULL,
    affiliate_comm_value character varying(1000) NOT NULL,
    description character varying(255),
    pay boolean NOT NULL,
    salary numeric(19,2) NOT NULL,
    affiliate_id bigint,
    value_type character varying(255) NOT NULL,
    salary_to_pay numeric(19,2) DEFAULT 0 NOT NULL
);

CREATE TABLE affiliate_link (
    id bigint NOT NULL,
    expiry_date timestamp without time zone NOT NULL,
    code character varying(36),
    user_id bigint
);

CREATE TABLE affiliate_link_purchaser (
    affiliate_link_id bigint NOT NULL,
    user_id bigint NOT NULL
);

CREATE TABLE affiliate_percent_info (
    user_info_id bigint NOT NULL,
    value integer,
    value_from integer,
    value_to integer,
    text_value character varying(1000)
);

CREATE TABLE category (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    group_id bigint NOT NULL,
    code character varying(255) NOT NULL,
    value_type character varying(255),
    filter_order integer DEFAULT 0 NOT NULL,
    display_as_filter boolean DEFAULT true NOT NULL,
    template character varying(255),
    category_condition text
);

CREATE TABLE category_content (
    id bigint NOT NULL,
    content text NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    opt_lock bigint NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    category_id bigint,
    name character varying(255) DEFAULT ''::character varying NOT NULL,
    code character varying(255) DEFAULT ''::character varying NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL
);

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE category_value (
    category_id bigint NOT NULL,
    caption character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    value_type character varying(255) DEFAULT 'FIX'::character varying NOT NULL,
    days_after_initial_deposit integer,
    max_age integer,
    min_age integer,
    days_after_final_payment integer,
    api_value character varying(25),
    id bigint DEFAULT nextval('hibernate_sequence'::regclass) NOT NULL
);

CREATE TABLE certificate (
    id bigint NOT NULL,
    file_uid character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    file_name character varying(255) NOT NULL,
    mime_type character varying(255) NOT NULL,
    size bigint NOT NULL,
    vendor_code character varying(255) NOT NULL,
    policy_meta_code character varying(255) NOT NULL,
    default_policy boolean,
    deleted boolean
);

CREATE TABLE certificate_country (
    certificate_id bigint NOT NULL,
    countries character varying(255)
);

CREATE TABLE certificate_state (
    certificate_id bigint NOT NULL,
    states character varying(255)
);

CREATE SEQUENCE company_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE company (
    id bigint DEFAULT nextval('company_id_seq'::regclass) NOT NULL,
    name character varying(255) NOT NULL,
    percent_type character varying(255) NOT NULL,
    tax_id character varying(255),
    payment_option character varying(255),
    bank_name character varying(255),
    bank_routing character varying(255),
    account character varying(255),
    paypal_email_address character varying(255),
    website character varying(255),
    deleted timestamp without time zone,
    deleted_by text
);

CREATE TABLE company_percent_info (
    company_id bigint NOT NULL,
    value bigint,
    value_from bigint,
    value_to bigint,
    text_value text
);

CREATE TABLE default_link_percent_info (
    system_settings_id bigint NOT NULL,
    value integer,
    value_from integer,
    value_to integer,
    text_value character varying(1000)
);

CREATE TABLE file_attachment (
    id bigint NOT NULL,
    file_uid character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    content oid NOT NULL,
    name character varying(255) NOT NULL,
    mime_type character varying(255) NOT NULL,
    size bigint NOT NULL,
    deleted boolean,
    file_name character varying(255)
);

CREATE TABLE filing_claim_contact (
    id bigint NOT NULL,
    filing_claim_page_id bigint NOT NULL,
    customer_service_number character varying(255) NOT NULL,
    customer_service_hours_of_operation character varying(255) NOT NULL,
    claims_filing_number character varying(255) NOT NULL,
    claims_hours_of_operation character varying(255) NOT NULL,
    website character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    fax character varying(255) NOT NULL,
    twenty_four_hour_emergency_assistance_numbers character varying(255) NOT NULL,
    mail_to character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    content text DEFAULT ''::text NOT NULL,
    create_date timestamp without time zone DEFAULT now() NOT NULL,
    modified_date timestamp without time zone DEFAULT now() NOT NULL,
    user_id bigint DEFAULT 2 NOT NULL,
    opt_lock bigint DEFAULT 1 NOT NULL
);

CREATE TABLE filing_claim_page (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    caption character varying(255) NOT NULL,
    content text NOT NULL,
    description character varying(1000) NOT NULL,
    page_type_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    opt_lock bigint NOT NULL,
    vendor_page_id bigint NOT NULL,
    phone_number character varying(50) NOT NULL,
    schedule_per_day character varying(255) NOT NULL,
    schedule_per_week character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);

CREATE TABLE "group" (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);

CREATE TABLE menu (
    id bigint NOT NULL,
    title character varying(255) NOT NULL,
    menu_type character varying(255) DEFAULT 'USER_MENU'::character varying NOT NULL
);

CREATE TABLE menu_item (
    id bigint NOT NULL,
    menu_id bigint,
    title character varying(255) NOT NULL,
    parent_menu_item_id bigint,
    url character varying(255),
    page_id bigint,
    level integer NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL
);

CREATE TABLE page (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    caption character varying(255) NOT NULL,
    content text NOT NULL,
    page_type_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    opt_lock bigint NOT NULL,
    description character varying(1000) DEFAULT ''::character varying NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);

CREATE TABLE page_type (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    template character varying(255) NOT NULL
);

CREATE TABLE persistent_logins (
    username character varying(64) NOT NULL,
    series character varying(64) NOT NULL,
    token character varying(64) NOT NULL,
    last_used timestamp without time zone NOT NULL
);

CREATE TABLE policy_meta (
    id bigint NOT NULL,
    active boolean NOT NULL,
    percent_type character varying(255),
    purchasable boolean NOT NULL,
    supports_zero_cancellation boolean NOT NULL,
    unique_code character varying(255) NOT NULL,
    vendor_id bigint NOT NULL,
    minimal_trip_cost numeric(19,2) DEFAULT NULL::numeric,
    required_deposit_date boolean DEFAULT false NOT NULL,
    policy_meta_page_id bigint,
    display_name character varying(255) DEFAULT 'No Name'::character varying NOT NULL,
    deleted_date timestamp with time zone,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_category (
    id bigint NOT NULL,
    description character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    category_id bigint,
    policy_meta_id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_category_content (
    id bigint NOT NULL,
    content text NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    opt_lock bigint NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    category_id bigint,
    policy_meta_category_id bigint,
    certificate_text text,
    policy_meta_plan_info_id bigint,
    policy_meta_custom_category_id bigint,
    policy_meta_restrictions_id bigint,
    "group" character varying(255),
    name character varying(255) DEFAULT ''::character varying NOT NULL,
    policy_meta_package_id bigint,
    package_options_id bigint
);

CREATE TABLE policy_meta_category_restriction (
    id bigint NOT NULL,
    restriction_permit character varying(255),
    restriction_type character varying(255) NOT NULL,
    min_value integer,
    max_value integer,
    policy_meta_category_id bigint NOT NULL,
    calculated_restrictions text,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_category_restriction_country (
    category_restriction_id bigint NOT NULL,
    countries character varying(255)
);

CREATE TABLE policy_meta_category_restriction_state (
    category_restriction_id bigint NOT NULL,
    states character varying(255)
);

CREATE TABLE policy_meta_category_value (
    policy_meta_category_id bigint NOT NULL,
    caption character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    value_type character varying(255) DEFAULT 'FIX'::character varying NOT NULL,
    days_after_initial_deposit integer,
    max_age integer,
    min_age integer,
    days_after_final_payment integer,
    api_value character varying(50),
    id bigint DEFAULT nextval('hibernate_sequence'::regclass) NOT NULL,
    secondary boolean DEFAULT false NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    fixed_cost numeric(19,2) DEFAULT NULL::numeric,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_code (
    id bigint NOT NULL,
    code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    policy_meta_id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_code_restriction (
    id bigint NOT NULL,
    restriction_permit character varying(255),
    restriction_type character varying(255) NOT NULL,
    min_value integer,
    max_value integer,
    policy_meta_code_id bigint NOT NULL,
    calculated_restrictions text,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_code_restriction_country (
    code_restriction_id bigint NOT NULL,
    countries character varying(255)
);

CREATE TABLE policy_meta_code_restriction_state (
    code_restriction_id bigint NOT NULL,
    states character varying(255)
);

CREATE TABLE policy_meta_package (
    id bigint NOT NULL,
    code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    policy_meta_id bigint NOT NULL,
    fixed_cost numeric(19,2) DEFAULT NULL::numeric,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_package_restriction (
    id bigint NOT NULL,
    restriction_permit character varying(255),
    restriction_type character varying(255) NOT NULL,
    min_value integer,
    max_value integer,
    policy_meta_package_id bigint NOT NULL,
    calculated_restrictions text,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_package_restriction_country (
    package_restriction_id bigint NOT NULL,
    countries character varying(255)
);

CREATE TABLE policy_meta_package_restriction_state (
    package_restriction_id bigint NOT NULL,
    states character varying(255)
);

CREATE TABLE policy_meta_package_value (
    id bigint NOT NULL,
    policy_meta_package_id bigint NOT NULL,
    policy_meta_category_id bigint NOT NULL,
    value character varying(255) NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_page (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    caption character varying(255) NOT NULL,
    content text NOT NULL,
    description character varying(1000) NOT NULL,
    page_type_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    opt_lock bigint NOT NULL,
    vendor_page_id bigint NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    certificate oid,
    certificate_modified_date timestamp without time zone,
    filling_claim_contact_id bigint
);

CREATE TABLE policy_meta_percent_info (
    policy_meta_id bigint NOT NULL,
    value integer,
    value_from integer,
    value_to integer,
    text_value character varying(1000)
);

CREATE TABLE policy_meta_plan_type (
    plan_type character varying(255) NOT NULL,
    policy_meta_id bigint NOT NULL
);

CREATE TABLE policy_meta_restriction (
    id bigint NOT NULL,
    restriction_permit character varying(255),
    restriction_type character varying(255) NOT NULL,
    policy_meta_id bigint NOT NULL,
    min_value integer,
    max_value integer,
    calculated_restrictions text,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_meta_restriction_country (
    restriction_id bigint NOT NULL,
    countries character varying(255)
);

CREATE TABLE policy_meta_restriction_state (
    restriction_id bigint NOT NULL,
    states character varying(255)
);

CREATE TABLE policy_quote_param (
    id bigint NOT NULL,
    type1 character varying(255),
    value_from_1 bigint,
    value_to_1 bigint,
    type2 character varying(255),
    value_from_2 bigint,
    value_to_2 bigint,
    type3 character varying(255),
    value_from_3 bigint,
    value_to_3 bigint,
    type4 character varying(255),
    value_from_4 bigint,
    value_to_4 bigint,
    type5 character varying(255),
    value_from_5 bigint,
    value_to_5 bigint,
    value bigint NOT NULL,
    policy_meta_id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_quote_param_restriction (
    id bigint NOT NULL,
    restriction_permit character varying(255),
    restriction_type character varying(255) NOT NULL,
    min_value integer,
    max_value integer,
    policy_quote_param_id bigint NOT NULL,
    calculated_restrictions text,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE policy_quote_param_restriction_country (
    policy_quote_param_restriction_id bigint NOT NULL,
    countries character varying(255)
);

CREATE TABLE policy_quote_param_restriction_state (
    policy_quote_param_restriction_id bigint NOT NULL,
    states character varying(255)
);

CREATE TABLE purchase (
    id bigint NOT NULL,
    order_request_id character varying(255),
    policy_number character varying(255) NOT NULL,
    purchase_date timestamp without time zone NOT NULL,
    total_price numeric(19,2) NOT NULL,
    affiliate_commission_id bigint,
    policy_meta_id bigint,
    quote_storage_id character varying(255),
    user_id bigint,
    quote_request_json character varying(20000) DEFAULT ''::character varying NOT NULL,
    purchase_request_json character varying(20000) NOT NULL,
    vendor_commission_id bigint,
    success boolean DEFAULT true NOT NULL,
    error_msg character varying(4000),
    purchase_uuid character varying(255) DEFAULT uuid_in((md5(((random())::text || (now())::text)))::cstring) NOT NULL,
    depart_date timestamp without time zone NOT NULL,
    trip_cost numeric(19,2) NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    middle_initials character varying(255),
    age integer,
    destination_country character varying(255) NOT NULL,
    return_date timestamp without time zone NOT NULL,
    resident_country character varying(255) NOT NULL,
    resident_state character varying(255),
    citizen_country character varying(255) NOT NULL,
    deposit_date timestamp without time zone,
    payment_date timestamp without time zone,
    note character varying(1000),
    purchase_type character varying(255) DEFAULT 'REAL'::character varying NOT NULL,
    cancel boolean DEFAULT false NOT NULL,
    birthday timestamp without time zone,
    "primary" boolean DEFAULT true NOT NULL,
    timezone_offset bigint DEFAULT 0 NOT NULL
);

CREATE TABLE purchase_params (
    purchase_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(4000) NOT NULL
);

CREATE TABLE purchase_traveler (
    purchase_id bigint NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    middle_initials character varying(255),
    age integer,
    birthday timestamp without time zone,
    "primary" boolean DEFAULT false NOT NULL
);

CREATE TABLE quote_storage (
    id character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    quote_request_json character varying(20000) NOT NULL,
    saved boolean,
    uid character varying(255),
    user_id bigint,
    deleted boolean DEFAULT false,
    affiliate_id bigint,
    type character varying(255),
    policy_unique_code character varying(255),
    original boolean DEFAULT false NOT NULL,
    system_saved boolean DEFAULT false
);

CREATE TABLE salary_correction (
    id bigint NOT NULL,
    salary numeric(19,2) NOT NULL,
    pay boolean,
    note character varying(1000),
    affiliate_id bigint NOT NULL,
    received_date timestamp without time zone NOT NULL
);

--

CREATE TABLE subcategory (
    id bigint NOT NULL,
    subcategory_name character varying(255) NOT NULL,
    subcategory_code character varying(255) NOT NULL,
    category_id bigint NOT NULL,
    template character varying(36) DEFAULT ''::character varying NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL
);

CREATE TABLE subcategory_value (
    id bigint NOT NULL,
    subcategory_value character varying(255),
    subcategory_id bigint NOT NULL,
    policy_meta_category_value_id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE system_settings (
    id bigint NOT NULL,
    default_link_percent_type character varying(255) DEFAULT 'NONE'::character varying,
    name character varying(255) NOT NULL,
    category1_id bigint NOT NULL,
    category2_id bigint NOT NULL,
    category3_id bigint NOT NULL,
    category4_id bigint NOT NULL,
    category5_id bigint NOT NULL,
    category6_id bigint NOT NULL,
    phone character varying(255)
);

CREATE TABLE unsupported_card_types (
    unsupported_card_type character varying(255) NOT NULL,
    vendor_id bigint NOT NULL
);

CREATE TABLE "user" (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    is_verified boolean NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    user_info_id bigint,
    deleted timestamp without time zone,
    created date,
    company_id bigint,
    affiliation_requested timestamp without time zone,
    affiliation_approved timestamp without time zone,
    affiliation_notified timestamp without time zone,
    deleted_by text
);

CREATE TABLE user_info (
    id bigint NOT NULL,
    address character varying(255),
    city character varying(255),
    contact_name character varying(255),
    country character varying(255),
    last_name character varying(255),
    percent_type character varying(255),
    phone character varying(255),
    state_or_province character varying(255),
    user_type character varying(255) NOT NULL,
    website character varying(255),
    zip_code character varying(255),
    autocomplete_purchase boolean DEFAULT false,
    confirm_notification boolean DEFAULT false,
    company boolean DEFAULT false NOT NULL,
    company_name character varying(255),
    tax_id character varying(255),
    payment_option text,
    bank_name text,
    bank_routing text,
    account text,
    paypal_email_address text
);

CREATE TABLE user_roles (
    role character varying(255) NOT NULL,
    user_id bigint NOT NULL
);

CREATE TABLE vendor (
    id bigint NOT NULL,
    code character varying(255) NOT NULL,
    icon oid,
    name character varying(255) NOT NULL,
    percent_type character varying(255),
    icon_last_modified timestamp without time zone,
    active boolean DEFAULT true NOT NULL,
    beneficiary_type character varying(255) DEFAULT 'SINGLE_BENEFICIARY'::character varying NOT NULL,
    age_from_depart_date boolean DEFAULT false NOT NULL,
    deleted_date timestamp with time zone,
    terms_and_conditions_is_active boolean DEFAULT false NOT NULL,
    terms_and_conditions_type character(4) DEFAULT 'LINK'::bpchar NOT NULL,
    terms_and_conditions_text text,
    test boolean DEFAULT false NOT NULL,
    test_user_ids text,
    created timestamp without time zone,
    modified timestamp without time zone
);

CREATE TABLE vendor_commission (
    id bigint NOT NULL,
    confirm boolean NOT NULL,
    description character varying(255),
    expected_commission numeric(19,2) NOT NULL,
    received_commission numeric(19,2),
    vendor_id bigint,
    value_type character varying(255) NOT NULL,
    commission_value character varying(1000),
    check_number character varying(255) DEFAULT ''::character varying,
    received_date timestamp without time zone
);

CREATE TABLE vendor_page (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    caption character varying(255) NOT NULL,
    content text NOT NULL,
    description character varying(1000) NOT NULL,
    page_type_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    opt_lock bigint NOT NULL,
    vendor_id bigint NOT NULL,
    rating numeric(19,2) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    deleted_date timestamp without time zone
);

CREATE TABLE vendor_percent_info (
    vendor_id bigint NOT NULL,
    value integer,
    value_from integer,
    value_to integer,
    text_value character varying(1000)
);

CREATE TABLE verification_token (
    id bigint NOT NULL,
    expiry_date timestamp without time zone NOT NULL,
    token character varying(36),
    token_type character varying(255) NOT NULL,
    verified boolean NOT NULL,
    user_id bigint
);

