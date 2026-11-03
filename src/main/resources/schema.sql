create table time
(
    id   bigint primary key not null auto_increment,
    time varchar(255) not null,
    deleted boolean not null default false
);

create table reservation
(
    id      bigint primary key not null auto_increment,
    name    varchar(255) not null,
    date    varchar(255) not null,
    time_id bigint not null,
    foreign key (time_id) references time (id),
    unique (date, time_id)
);
