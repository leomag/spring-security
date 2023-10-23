CREATE TABLE users
(
    id       BIGSERIAL,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL,
    email    VARCHAR(50) UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   SERIAL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id INT    NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

INSERT INTO users (username, password, email)
VALUES ('oleg', '$2a$10$o2US6wzNoMFvs2UsaGcBDOjJ9ZH2JfR9u2RkUsJpl6Chypb37uK42', 'oleg@gmail.com'),
       ('dima', '$2a$10$9ngddof1HmGxYHB2ctJHHOdSQFHwrBJA5jR9Rsy7ShcsHW.i3HkkW', 'admin@gmail.com');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);