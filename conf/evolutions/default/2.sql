# Users schema

# --- !Ups

CREATE SEQUENCE "user_id_seq";
CREATE TABLE "users" (
    "id" integer NOT NULL DEFAULT nextval('user_id_seq'),
    "name" varchar(255),
    "password_hash" varchar(255)
);

# --- !Downs

DROP TABLE "users";
DROP SEQUENCE "user_id_seq";