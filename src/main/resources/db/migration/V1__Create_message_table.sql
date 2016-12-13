CREATE TABLE message (
  id       SERIAL PRIMARY KEY,
  sender   INTEGER   NOT NULL,
  receiver INTEGER   NOT NULL,
  message  TEXT      NOT NULL,
  ts       TIMESTAMP NOT NULL
);