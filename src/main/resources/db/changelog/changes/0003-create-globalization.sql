--liquibase formatted sql

--changeset knock:0003-create-globalization
CREATE SEQUENCE IF NOT EXISTS question_globalization_id_seq;
CREATE TABLE IF NOT EXISTS question_globalization (
    id              BIGINT  DEFAULT NEXTVAL('question_globalization_id_seq'::regclass)    NOT NULL    PRIMARY KEY,
    question_id     BIGINT NOT NULL,
    locale          VARCHAR(10)  NOT NULL,
    content         VARCHAR(500) NOT NULL,
    CONSTRAINT fk_question_globalization_question FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE,
    CONSTRAINT uk_question_globalization_locale UNIQUE (question_id, locale)
);
ALTER SEQUENCE question_globalization_id_seq OWNED BY question_globalization.id;

COMMENT ON TABLE  question_globalization             IS '질문 다국어';
COMMENT ON COLUMN question_globalization.question_id IS '질문 테이블 아이디';
COMMENT ON COLUMN question_globalization.locale      IS '언어';
COMMENT ON COLUMN question_globalization.content     IS '질문내용';

