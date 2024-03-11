CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username text NOT NULL
);

CREATE TABLE mobile_phone (
    phone_id SERIAL PRIMARY KEY,
    model_name text NOT NULL
);

CREATE TABLE mobile_phone_reservation (
    reservation_id SERIAL PRIMARY KEY,
    phone_id int REFERENCES mobile_phone UNIQUE,
    user_id int REFERENCES users,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE reservation_outbox (
    msg_id SERIAL PRIMARY KEY,
    phone_id int REFERENCES mobile_phone,
    user_id int REFERENCES users,
    created_at TIMESTAMP NOT NULL,
    reservation_status varchar(16) NOT NULL
);

INSERT INTO users (username) VALUES
('Mike'),
('John'),
('Kowalski');

INSERT INTO mobile_phone (model_name) VALUES
('Samsung Galaxy S9'),
('Samsung Galaxy S8'),
('Samsung Galaxy S8'),
('Motorola Nexus 6'),
('Oneplus 9'),
('Apple iPhone 13'),
('Apple iPhone 12'),
('Apple iPhone 11'),
('iPhone X'),
('Nokia 3310')