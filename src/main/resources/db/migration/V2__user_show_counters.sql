CREATE TABLE user_show_counters (
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    user_id VARCHAR(120) NOT NULL,
    confirmed_seats INTEGER NOT NULL DEFAULT 0 CHECK (confirmed_seats >= 0),
    PRIMARY KEY (show_id, user_id)
);
