create sequence if not exists sources_id_seq start 1 increment 1;

create table sources
(
  id              BIGINT DEFAULT nextval('sources_id_seq') PRIMARY KEY,
  source          varchar(255) not null,
  source_language varchar(16) not null
);

create unique index if not exists sources_source_idx on sources(source);
create index if not exists sources_source_language_idx on sources(source_language);

create sequence if not exists targets_id_seq start 1 increment 1;

create table targets
(
  id              BIGINT DEFAULT nextval('targets_id_seq') PRIMARY KEY,
  source          varchar(255) not null,
  target          varchar(255) not null,
  source_language varchar(16) not null,
  target_language varchar(16) not null
);

create unique index if not exists targets_source_target_idx on targets(source, target);

create index if not exists targets_source_language_target_language_idx on targets(source_language, target_language);