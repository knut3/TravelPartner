# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table picture (
  id                        bigint not null,
  owner_id                  bigint,
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

alter table user add constraint fk_user_profilePicture_1 foreign key (profile_picture_id) references picture (id) on delete restrict on update restrict;
create index ix_user_profilePicture_1 on user (profile_picture_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists picture;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists picture_seq;

drop sequence if exists user_seq;

