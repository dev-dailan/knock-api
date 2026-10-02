--liquibase formatted sql

--changeset knock:0001-create-question logicalFilePath:db/changelog/changes/0001-create-question.sql
CREATE SEQUENCE IF NOT EXISTS question_id_seq;
CREATE TABLE IF NOT EXISTS question (
    id          BIGINT  DEFAULT NEXTVAL('question_id_seq'::regclass)    NOT NULL    PRIMARY KEY,
    content     VARCHAR(500) NOT NULL
);

COMMENT ON COLUMN question.content is '질문내용';
