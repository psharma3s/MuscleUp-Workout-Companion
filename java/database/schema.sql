BEGIN TRANSACTION;

DROP TABLE IF EXISTS users, user_gym_visits;

CREATE TABLE users (
	user_id SERIAL,
	username varchar(50) NOT NULL UNIQUE,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	name varchar(50) NOT NULL,
	email varchar(64),
	workout_goals(500),
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);

CREATE TABLE user_gym_visits (
    visit_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    check_in_time TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

COMMIT TRANSACTION;
