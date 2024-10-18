create table recipes
(
    id bigserial primary key,
    username text not null,
    title text not null,
    instructions text not null,
    servings int,
    description text,
    unique(username, title)
);

create index recipes_username_index on recipes (username);
create index recipes_title_index on recipes (title);

create table ingredients
(
    id bigserial primary key,
    recipe_id bigserial not null,
    value float not null,
    unit text not null,
    type text not null
);

create index ingredients_recipes_fk on ingredients (recipe_id);
