--liquibase formatted sql

--changeset knock:0004-create-choice
CREATE SEQUENCE IF NOT EXISTS choice_id_seq;
CREATE TABLE IF NOT EXISTS choice (
    id              BIGINT  DEFAULT NEXTVAL('choice_id_seq'::regclass)    NOT NULL    PRIMARY KEY,
    level           VARCHAR(10)  NOT NULL,  -- EASY, MEDIUM, HARD
    first_option    VARCHAR(200) NOT NULL,
    second_option   VARCHAR(200) NOT NULL
);
ALTER SEQUENCE choice_id_seq OWNED BY choice.id;

COMMENT ON TABLE  choice               IS '선택';
COMMENT ON COLUMN choice.level         IS '선택 난이도';
COMMENT ON COLUMN choice.first_option  IS '선택1';
COMMENT ON COLUMN choice.second_option IS '선택2';

CREATE SEQUENCE IF NOT EXISTS choice_globalization_id_seq;
CREATE TABLE IF NOT EXISTS choice_globalization (
    id              BIGINT  DEFAULT NEXTVAL('choice_globalization_id_seq'::regclass)    NOT NULL    PRIMARY KEY,
    choice_id       BIGINT NOT NULL,
    locale          VARCHAR(10)  NOT NULL,
    first_option    VARCHAR(200) NOT NULL,
    second_option   VARCHAR(200) NOT NULL,
    CONSTRAINT fk_choice_globalization_choice FOREIGN KEY (choice_id) REFERENCES choice (id) ON DELETE CASCADE,
    CONSTRAINT uk_choice_globalization_locale UNIQUE (choice_id, locale)
);
ALTER SEQUENCE choice_globalization_id_seq OWNED BY choice_globalization.id;

COMMENT ON TABLE  choice_globalization                 IS '선택 다국어';
COMMENT ON COLUMN choice_globalization.choice_id       IS '선택 테이블 아이디';
COMMENT ON COLUMN choice_globalization.locale          IS '언어';
COMMENT ON COLUMN choice_globalization.first_option    IS '선택1';
COMMENT ON COLUMN choice_globalization.second_option   IS '선택2';

