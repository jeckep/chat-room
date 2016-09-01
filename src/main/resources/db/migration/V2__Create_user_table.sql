create table user(
    id SERIAL primary key,
    name TEXT,
    surname TEXT,
    email TEXT unique
);