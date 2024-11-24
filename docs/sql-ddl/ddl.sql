CREATE DATABASE mycards;

CREATE TABLE cards (
    card_id BIGINT NOT NULL AUTO_INCREMENT,
    bank_name VARCHAR(255),
    card_number VARCHAR(255) UNIQUE,
    expiry VARCHAR(255),
    issue_date VARCHAR(255),
    card_type VARCHAR(255),
    card_name VARCHAR(255),
    PRIMARY KEY (card_id)
);


CREATE TABLE chat_history (
    chat_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    input_message VARCHAR(1000),
    output_message VARCHAR(1000),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_timestamp TIMESTAMP,
    updated_timestamp TIMESTAMP,
    PRIMARY KEY (chat_id)
);


CREATE TABLE offers (
    offer_id VARCHAR(255) NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    type VARCHAR(255),
    redeem_type VARCHAR(255),
    start_date VARCHAR(255),
    end_date VARCHAR(255),
    quantity VARCHAR(255),
    quantity_type VARCHAR(255),
    offer_code VARCHAR(255),
    conditions VARCHAR(255),
    created_by VARCHAR(255),
    created_timestamp TIMESTAMP,
    updated_by VARCHAR(255),
    updated_timestamp TIMESTAMP,
    PRIMARY KEY (offer_id)
);


CREATE TABLE otp_details (
    otp_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    email_otp VARCHAR(255),
    mobile_otp VARCHAR(255),
    created_timestamp TIMESTAMP,
    expiry_timestamp TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    updated_timestamp TIMESTAMP,
    email VARCHAR(255),
    mobile VARCHAR(255),
    PRIMARY KEY (otp_id)
);

CREATE TABLE partners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    mobile VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255),
    company_name VARCHAR(255),
    company_address VARCHAR(255),
    date_of_incorporation DATE,
    license VARCHAR(255),
    cin VARCHAR(255) UNIQUE,
    gst_no VARCHAR(255) UNIQUE
);

CREATE TABLE partner_activities (
    activity_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partner_id BIGINT,
    event_type VARCHAR(255),
    ip_address VARCHAR(255),
    event_timestamp DATETIME,
    event_details VARCHAR(255)
);


CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    password VARCHAR(255) NOT NULL,
    mobile VARCHAR(255),
    email VARCHAR(255),
    status VARCHAR(255),
    created_by VARCHAR(255),
    created_timestamp TIMESTAMP,
    updated_by VARCHAR(255),
    updated_timestamp TIMESTAMP,
    PRIMARY KEY (user_id)
);


CREATE TABLE user_activity (
    activity_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    event_type VARCHAR(255),
    event_timestamp TIMESTAMP,
    ip_address VARCHAR(255),
    event_details VARCHAR(255),
    PRIMARY KEY (activity_id)
);


CREATE TABLE user_cards (
    user_card_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    card_id VARCHAR(255),
    card_number VARCHAR(255),
    expiry VARCHAR(255),
    bank_name VARCHAR(255),
    issue_date VARCHAR(255),
    card_type VARCHAR(255),
    card_name VARCHAR(255),
    created_by VARCHAR(255),
    created_timestamp TIMESTAMP,
    updated_by VARCHAR(255),
    updated_timestamp TIMESTAMP,
    PRIMARY KEY (user_card_id)
);


CREATE TABLE user_details (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    salutation VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    gender VARCHAR(255),
    dob VARCHAR(255),
    terms_accepted VARCHAR(255),
    terms_accepted_timestamp TIMESTAMP,
    created_by VARCHAR(255),
    created_timestamp TIMESTAMP,
    updated_by VARCHAR(255),
    updated_timestamp TIMESTAMP,
    PRIMARY KEY (user_id)
);


CREATE TABLE user_preferences (
    preference_type VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    PRIMARY KEY (preference_type)
);

