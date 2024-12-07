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


ALTER TABLE workout_metrics
ADD COLUMN distance VARCHAR(255),
ADD COLUMN workout_type VARCHAR(255),
ADD COLUMN workout_duration INT,
ADD COLUMN exercise VARCHAR(255),
ADD COLUMN sets INT;


GRANT INSERT, SELECT, UPDATE ON TABLE workout_metrics TO final_capstone_appuser;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE workout_metrics_metric_id_seq TO final_capstone_appuser;

ALTER TABLE workout_metrics
ADD COLUMN sets INT;

CREATE TABLE classes (
    class_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    class_date DATE NOT NULL,
    class_time TIME NOT NULL,
    instructor VARCHAR(255),
    duration VARCHAR(50),
    calories_burned VARCHAR(50)
);

CREATE TABLE class_registrations (
    registration_id SERIAL PRIMARY KEY,
    class_id INT NOT NULL REFERENCES classes(class_id),
    user_id INT NOT NULL REFERENCES users(user_id),
    UNIQUE (class_id, user_id)
);

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE classes TO final_capstone_appuser;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE class_registrations TO final_capstone_appuser;

GRANT USAGE, SELECT, UPDATE ON SEQUENCE classes_class_id_seq TO final_capstone_appuser;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE class_registrations_registration_id_seq TO final_capstone_appuser;

COMMIT TRANSACTION;
