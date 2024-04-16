create sequence if not exists await_translate_seq start 1 increment 1;

create table await_translate
(
  id   BIGINT DEFAULT nextval('await_translate_seq') PRIMARY KEY,
  word varchar(255) not null
);


create unique index if not exists await_translate_word_idx on await_translate(word);