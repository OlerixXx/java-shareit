CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320), first_name varchar(100),
    last_name varchar(100),
    registration_date timestamp, state varchar(50)
    );