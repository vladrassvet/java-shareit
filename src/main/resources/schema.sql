DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
email VARCHAR(128) NOT NULL,
name VARCHAR(128) NOT NULL,
CONSTRAINT pk_user PRIMARY KEY(id),
CONSTRAINT UQ_USER_EMAIL UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS requests(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
description VARCHAR,
created DATETIME NOT NULL,
requestor_id BIGINT REFERENCES users (id),
CONSTRAINT pk_requests PRIMARY KEY (id),
CONSTRAINT fk_request_user_id_to_user
FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
description VARCHAR(2000) NOT NULL,
is_available boolean NOT NULL,
user_id BIGINT REFERENCES users (id),
request_id BIGINT REFERENCES requests (id),
CONSTRAINT pk_item PRIMARY KEY (id),
CONSTRAINT fk_owner_id_to_user
FOREIGN KEY (user_id) REFERENCES users (id),
CONSTRAINT fk_request_item_id
FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS comments(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
text VARCHAR(2000) NOT NULL,
item_id BIGINT REFERENCES items (id),
author_id BIGINT REFERENCES users (id),
created timestamp,
FOREIGN KEY (item_id) REFERENCES items (id),
FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
item_id BIGINT REFERENCES items (id),
booking_user_id BIGINT REFERENCES users (id),
data_start timestamp,
data_end timestamp,
status VARCHAR(50),
FOREIGN KEY (item_id) REFERENCES items (id),
FOREIGN KEY (booking_user_id) REFERENCES users (id)
);


