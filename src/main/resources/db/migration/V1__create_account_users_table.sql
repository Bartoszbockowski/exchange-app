CREATE TABLE account_users (
                               id BIGSERIAL PRIMARY KEY,
                               version BIGINT DEFAULT 0,
                               name VARCHAR(255) NOT NULL,
                               lastname VARCHAR(255) NOT NULL,
                               account_balance_pln NUMERIC(19, 4) default 0,
                               account_balance_usd NUMERIC(19, 4) default 0,
                               username VARCHAR(255) UNIQUE NOT NULL,
                               password VARCHAR(255) NOT NULL
);