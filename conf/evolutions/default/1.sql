# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table location (
  id                        bigint not null,
  longitude                 float,
  latitude                  float,
  user_id                   bigint,
  date_time                 timestamp,
  constraint pk_location primary key (id))
;

create table message (
  id                        bigint not null,
  sender_id                 bigint,
  recipient_id              bigint,
  message                   varchar(255),
  date_time_sent            timestamp,
  is_read                   boolean,
  constraint pk_message primary key (id))
;

create table picture (
  id                        varchar(255) not null,
  owner_id                  bigint,
  width                     integer,
  height                    integer,
  constraint pk_picture primary key (id))
;

create table users (
  id                        bigint not null,
  first_name                varchar(255),
  gender                    varchar(255),
  profile_picture_id        varchar(255),
  current_location_id       bigint,
  constraint pk_users primary key (id))
;

create sequence location_seq;

create sequence message_seq;

create sequence picture_seq;

create sequence users_seq;

alter table message add constraint fk_message_sender_1 foreign key (sender_id) references users (id) on delete restrict on update restrict;
create index ix_message_sender_1 on message (sender_id);
alter table message add constraint fk_message_recipient_2 foreign key (recipient_id) references users (id) on delete restrict on update restrict;
create index ix_message_recipient_2 on message (recipient_id);
alter table users add constraint fk_users_profilePicture_3 foreign key (profile_picture_id) references picture (id) on delete restrict on update restrict;
create index ix_users_profilePicture_3 on users (profile_picture_id);
alter table users add constraint fk_users_currentLocation_4 foreign key (current_location_id) references location (id) on delete restrict on update restrict;
create index ix_users_currentLocation_4 on users (current_location_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists location;

drop table if exists message;

drop table if exists picture;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists location_seq;

drop sequence if exists message_seq;

drop sequence if exists picture_seq;

drop sequence if exists users_seq;

