/*create table authority
(
    name varchar(50) not null
        constraint authority_pkey
            primary key
);

create table users
(
    id                 bigint      not null
        constraint users_pkey
            primary key,
    created_by         varchar(50) not null,
    created_date       timestamp,
    last_modified_by   varchar(50),
    last_modified_date timestamp,
    activated          boolean     not null,
    activation_key     varchar(20),
    email              varchar(254)
        constraint uk_6dotkott2kjsp8vw4d0m25fb7
            unique,
    first_name         varchar(50),
    image_url          varchar(256),
    lang_key           varchar(10),
    last_name          varchar(50),
    login              varchar(50) not null
        constraint uk_ow0gan20590jrb00upg3va2fn
            unique,
    password_hash      varchar(60) not null,
    reset_date         timestamp,
    reset_key          varchar(20)
);

create table users_authority
(
    users_id       bigint      not null
        constraint user_authority_foreign_key
            references users,
    authority_name varchar(50) not null
        constraint fk4v17qlb0pqsorktcmltp5js2a
            references authority,
    constraint users_authority_pkey
        primary key (users_id, authority_name)
);
*/
