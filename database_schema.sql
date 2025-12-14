CREATE TABLE users (
    user_id NUMBER PRIMARY KEY,
    full_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    password_hash VARCHAR2(255) NOT NULL,
    user_type VARCHAR2(20) CHECK (user_type IN ('TRAINER', 'USER')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE fitness_plans (
    plan_id NUMBER PRIMARY KEY,
    trainer_id NUMBER NOT NULL,
    title VARCHAR2(200) NOT NULL,
    description VARCHAR2(1000),
    price NUMBER(10,2) NOT NULL,
    duration_days NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trainer FOREIGN KEY (trainer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE SEQUENCE plan_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE subscriptions (
    subscription_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    plan_id NUMBER NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(20) DEFAULT 'ACTIVE',
    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES fitness_plans(plan_id) ON DELETE CASCADE,
    CONSTRAINT unique_subscription UNIQUE (user_id, plan_id)
);

CREATE SEQUENCE subscription_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE follows (
    follow_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    trainer_id NUMBER NOT NULL,
    followed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_follower FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_followed_trainer FOREIGN KEY (trainer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT unique_follow UNIQUE (user_id, trainer_id)
);

CREATE SEQUENCE follow_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE auth_tokens (
    token_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    token VARCHAR2(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE SEQUENCE token_seq START WITH 1 INCREMENT BY 1;

INSERT INTO users (user_id, full_name, email, password_hash, user_type)
VALUES (user_seq.NEXTVAL, 'John Trainer', 'john@trainer.com', 'hashed_password_123', 'TRAINER');

INSERT INTO users (user_id, full_name, email, password_hash, user_type)
VALUES (user_seq.NEXTVAL, 'Sarah Fitness', 'sarah@trainer.com', 'hashed_password_456', 'TRAINER');

INSERT INTO users (user_id, full_name, email, password_hash, user_type)
VALUES (user_seq.NEXTVAL, 'Mike User', 'mike@user.com', 'hashed_password_789', 'USER');

INSERT INTO users (user_id, full_name, email, password_hash, user_type)
VALUES (user_seq.NEXTVAL, 'Emma Client', 'emma@user.com', 'hashed_password_101', 'USER');

INSERT INTO fitness_plans (plan_id, trainer_id, title, description, price, duration_days)
VALUES (plan_seq.NEXTVAL, 1, 'Fat Loss Beginner Plan', 'Perfect for beginners looking to lose weight', 49.99, 30);

INSERT INTO fitness_plans (plan_id, trainer_id, title, description, price, duration_days)
VALUES (plan_seq.NEXTVAL, 1, 'Muscle Building Advanced', 'Advanced muscle building program', 79.99, 60);

INSERT INTO fitness_plans (plan_id, trainer_id, title, description, price, duration_days)
VALUES (plan_seq.NEXTVAL, 2, 'Yoga Flow for Flexibility', 'Improve flexibility with daily yoga', 39.99, 30);

COMMIT;

