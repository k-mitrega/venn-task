CREATE TABLE load_attempts (
    domain_id UUID NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    load_amount DECIMAL(38, 2) NOT NULL,
    time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_load_attempts PRIMARY KEY (domain_id),
    CONSTRAINT uk_load_attempts_customer_external_id UNIQUE (customer_id, external_id)
);

CREATE TABLE loads (
    domain_id UUID NOT NULL,
    load_attempt_domain_id UUID NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    load_amount DECIMAL(38, 2) NOT NULL,
    time TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_loads PRIMARY KEY (domain_id),
    CONSTRAINT fk_loads_load_attempt FOREIGN KEY (load_attempt_domain_id) REFERENCES load_attempts (domain_id)
);

CREATE TABLE customer_usage (
    domain_id UUID NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    daily_period_start TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    daily_amount_used DECIMAL(38, 2) NOT NULL,
    weekly_period_start TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    weekly_amount_used DECIMAL(38, 2) NOT NULL,
    daily_loads_used INTEGER NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_customer_usage PRIMARY KEY (domain_id),
    CONSTRAINT uk_customer_usage_customer_id UNIQUE (customer_id)
);
