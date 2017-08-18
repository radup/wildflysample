#Wildfly sample

This is a full sample of a Wildfly application, using JPA, EJB'S , JAX-RS

#REST Api

@GET
http://{yourhost}/api/movies
@GET
http://{yourhost}/api/movies/{id}
@POST
http://{yourhost}/api/movies
@PUT
http://{yourhost}/api/movies/{id}

JSON to post/put
{
    "name" : "Lord of the rings"
}


#Database

Database should contain one table called "Movie", the provided standalone configuration targets POSTGREsql database

Script to create:

create table Movie (
	id bigserial not null primary key,
	name varchar(25) not null unique
);