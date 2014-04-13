# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table picture (
  id                        bigint not null,
  url                       varchar(255),
  width                     integer,
  height                    integer,
  constraint pk_picture primary key (id))
;

create table user (
  id                        bigint not null,
  first_name                varchar(255),
  gender                    varchar(255),
  profile_picture_id        bigint,
  latitude                  float,
  longitude                 float,
  constraint pk_user primary key (id))
;

create sequence picture_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists picture;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists picture_seq;

drop sequence if exists user_seq;

