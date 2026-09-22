-- Run while connected to database: my_database
-- (In pgAdmin/DBeaver: create DB my_database first, then open SQL editor on it)

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    age INT,
    email VARCHAR(100)
);

INSERT INTO users (name, age, email) VALUES
('John', 30, 'john@example.com'),
('Alice', 25, 'alice@example.com'),
('Bob', 35, 'bob@example.com');

SELECT * FROM users;

DELETE FROM users WHERE name = 'Bob';

SELECT * FROM users;
