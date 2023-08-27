DROP TABLE IF EXISTS STATS CASCADE;

CREATE TABLE IF NOT EXISTS STATS (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  app VARCHAR(255) NOT NULL,
  uri VARCHAR(512) NOT NULL,
  ip VARCHAR(512) NOT NULL,
  hit_date TIMESTAMP NOT NULL
);