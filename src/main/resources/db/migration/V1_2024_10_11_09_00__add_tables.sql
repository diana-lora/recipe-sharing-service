create table users
(
    id bigserial primary key,
    username text not null,
    password text not null,
    email text not null,
    unique(email),
    unique(username)
);

create table recipes
(
    id bigserial primary key,
    user_id bigserial not null,
    title text not null,
    instructions text not null,
    servings int,
    description text,
    unique(user_id, title)
);

create index recipes_users_fk on recipes (user_id);

create table ingredients
(
    id bigserial primary key,
    recipe_id bigserial not null,
    value float not null,
    unit text not null,
    type text not null
);

create index ingredients_recipes_fk on ingredients (recipe_id);
