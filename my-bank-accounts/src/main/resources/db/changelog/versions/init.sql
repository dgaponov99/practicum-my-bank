--liquibase formatted sql

-- changeset dgaponov99:001-init
create table account
(
    username   varchar(255) primary key,
    name       varchar(150) not null,
    birth_date date         not null,
    balance    bigint       not null check ( balance >= 0 )
);
