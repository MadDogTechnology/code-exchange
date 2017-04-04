-- psql -h localhost -U postgres --file src/sql/init.sql

DROP DATABASE IF EXISTS dbfor30;
CREATE DATABASE dbfor30;
DROP DATABASE IF EXISTS dbfor20;
CREATE DATABASE dbfor20;


\connect dbfor30
-- DROP SEQUENCE IF EXISTS pointnames_oneid_seq;
CREATE SEQUENCE pointnames_oneid_seq;
CREATE TABLE pointnames (
    oneid   integer PRIMARY KEY default nextval('pointnames_oneid_seq'),
    name    varchar(256)
);

\connect dbfor20
-- DROP SEQUENCE IF EXISTS pointnames_oneid_seq;
CREATE SEQUENCE pointnames_oneid_seq;
CREATE TABLE pointnames (
    oneid   integer PRIMARY KEY default nextval('pointnames_oneid_seq'),
    name    varchar(256)
);

