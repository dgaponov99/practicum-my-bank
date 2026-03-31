--liquibase formatted sql

-- changeset dgaponov99:001-init
create table account
(
    username   varchar(255) primary key,
    name       varchar(150) not null,
    birth_date date         not null,
    balance    bigint       not null check ( balance >= 0 )
);

-- changeset dgaponov99:001-init-outbox
create table notification_outbox
(
    id            uuid primary key,
    payload       jsonb     not null,
    retry_count   integer   not null default 0,
    next_retry_at timestamp not null default current_timestamp,
    created_at    timestamp not null default current_timestamp,
    updated_at    timestamp not null default current_timestamp
);
