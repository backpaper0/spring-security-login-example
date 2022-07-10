create table accounts (
    id bigint primary key,
    username varchar(20) not null,
    expiration_date date not null,
    authentication_failure_count int not null,
    locked_until timestamp,
    enabled boolean not null
);

create unique index on accounts (username);

create table account_passwords (
    account_id bigint primary key,
    hashed_password varchar(200) not null,
    expiration_date date not null,
    needs_to_change boolean not null,
    foreign key (account_id) references accounts (id) on delete cascade
);

create table account_authorities (
    id identity,
    account_id bigint not null,
    authority varchar(50),
    foreign key (account_id) references accounts (id) on delete cascade
);
