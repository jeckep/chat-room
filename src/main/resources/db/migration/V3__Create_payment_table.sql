CREATE TABLE payment (
  id      SERIAL PRIMARY KEY,
  amount  BIGINT    NOT NULL,
  ts      TIMESTAMP NOT NULL,
  paid    BOOLEAN   NOT NULL,
  user_id INTEGER   NOT NULL
);

ALTER TABLE ONLY payment
  ADD CONSTRAINT fkscc8b7tjqof2t1fn8dxxq60yx FOREIGN KEY (user_id) REFERENCES chatuser (id);
