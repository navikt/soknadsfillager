CREATE TABLE documents
(
    id        VARCHAR(255)  NOT NULL,
    data      BYTEA,
    opprettet TIMESTAMP WITH TIME ZONE NOT NULL default (now() at time zone 'UTC'),
    PRIMARY KEY (id)
);
