create sequence if not exists translations_id_seq start 1 increment 1;

create table translations
(
  id              BIGINT DEFAULT nextval('translations_id_seq') PRIMARY KEY,
  source          varchar(255) not null,
  target          varchar(255) not null,
  source_language varchar(16)  not null,
  target_language varchar(16)  not null
);

create unique index if not exists targets_source_target_idx on translations(source, target);

create index if not exists targets_source_language_target_language_idx on translations(source_language, target_language);