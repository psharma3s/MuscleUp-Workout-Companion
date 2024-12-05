BEGIN TRANSACTION;

DROP TABLE IF EXISTS users;

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

CREATE TABLE workout_metrics (
    metric_id SERIAL,
    user_id INT NOT NULL,
    equipment_used VARCHAR(255) NOT NULL,
    weights INT NOT NULL,
    reps INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_metric PRIMARY KEY (metric_id),
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
CREATE TABLE user_gym_visits (
    visit_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    check_in_time TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


COMMIT TRANSACTION;
