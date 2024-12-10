BEGIN TRANSACTION;


DROP TABLE IF EXISTS users, workout_metrics, user_gym_visits, classes, class_registrations;


CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(64),
    workout_goals VARCHAR(500),
    profile_picture VARCHAR(500);
);


CREATE TABLE workout_metrics (
    metric_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    equipment_used VARCHAR(255) NOT NULL,
    weights INT NOT NULL,
    reps INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    distance VARCHAR(255),
    workout_type VARCHAR(255),
    workout_duration INT,
    exercise VARCHAR(255),
    sets INT,
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);


CREATE TABLE user_gym_visits (
    visit_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    check_in_time TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP,
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);


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


GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE workout_metrics, classes, class_registrations TO final_capstone_appuser;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE workout_metrics_metric_id_seq, classes_class_id_seq, class_registrations_registration_id_seq TO final_capstone_appuser;

ALTER TABLE user_gym_visits
ADD COLUMN visit_status VARCHAR(20) DEFAULT 'Checked In',
ADD COLUMN visit_duration_minutes INT;

COMMIT TRANSACTION;
