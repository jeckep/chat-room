create table message(
    id SERIAL primary key,
    sender integer,
    receiver integer,
    message TEXT,
    ts timestamp
);