CREATE TABLE chatuser (
  id      SERIAL PRIMARY KEY,
  name    TEXT,
  surname TEXT,
  email   TEXT UNIQUE NOT NULL,
  picture TEXT
);