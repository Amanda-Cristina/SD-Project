CREATE TABLE IF NOT EXISTS users(
	id int not null AUTO_INCREMENT primary key,
	cpf varchar(255) not null,
	name varchar(255) not null,
	password varchar(255) not null
);