create table users
(
    id bigserial primary key,
    username text not null,
    password text not null,
    email text not null,
    unique(email),
    unique(username)
);
