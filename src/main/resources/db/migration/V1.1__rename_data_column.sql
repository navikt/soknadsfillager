ALTER TABLE documents
    RENAME COLUMN data TO document;

ALTER TABLE documents
    RENAME COLUMN opprettet TO created;

