create table message(
    id SERIAL primary key,
    from integer,
    to integer,
    message TEXT
    ts timestamp
);