DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY, name varchar(100), email varchar(100), registration_date timestamp);