-- psql -h localhost -U postgres --file src/sql/list.sql

\connect dbfor30
SELECT oneid, name FROM pointnames;

\connect dbfor20
SELECT oneid, name FROM pointnames;

