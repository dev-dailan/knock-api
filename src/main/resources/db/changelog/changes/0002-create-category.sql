--liquibase formatted sql

--changeset knock:0002-add-question-content-unique
ALTER TABLE question ADD CONSTRAINT uk_question_content UNIQUE (content);

ALTER TABLE question ADD COLUMN categories varchar[];
COMMENT ON COLUMN question.categories is '카테고리';

--changeset knock:0002-create-question-category
CREATE SEQUENCE IF NOT EXISTS question_category_id_seq;
CREATE TABLE IF NOT EXISTS question_category (
    id              BIGINT  DEFAULT NEXTVAL('question_category_id_seq'::regclass)    NOT NULL    PRIMARY KEY,
    question_id     BIGINT NOT NULL,
    category        VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_question_category_question FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE,
    CONSTRAINT uk_question_category UNIQUE (question_id, category)
);
ALTER SEQUENCE question_category_id_seq OWNED BY question_category.id;

COMMENT ON TABLE  question_category             IS '질문 카테고리';
COMMENT ON COLUMN question_category.question_id IS '질문 테이블 아이디';
COMMENT ON COLUMN question_category.category    IS '카테고리';
