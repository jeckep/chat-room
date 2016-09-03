create table chatuser(
    id SERIAL primary key,
    name TEXT,
    surname TEXT,
    email TEXT unique,
    picture TEXT
);