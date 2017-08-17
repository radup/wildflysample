create table Movie (
	id bigserial not null primary key,
	name varchar(25) not null unique
);