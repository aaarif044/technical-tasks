CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE shows (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    price_paise BIGINT NOT NULL CHECK (price_paise >= 0),
    per_user_limit INTEGER NOT NULL DEFAULT 4 CHECK (per_user_limit > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    seat_number VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_show_seat UNIQUE(show_id, seat_number),
    CONSTRAINT ck_seat_status CHECK (status IN ('AVAILABLE','HELD','CONFIRMED'))
);
CREATE INDEX idx_seats_show_status ON seats(show_id, status);

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL REFERENCES shows(id),
    user_id VARCHAR(120) NOT NULL,
    amount_paise BIGINT NOT NULL CHECK (amount_paise >= 0),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMPTZ,
    CONSTRAINT ck_reservation_status CHECK (status IN ('CONFIRMED','CANCELLED'))
);
CREATE INDEX idx_reservations_show_user_status ON reservations(show_id, user_id, status);

CREATE TABLE reservation_seats (
    reservation_id BIGINT NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats(id),
    PRIMARY KEY (reservation_id, seat_id),
    CONSTRAINT uq_reservation_seat UNIQUE(seat_id)
);

CREATE TABLE idempotency_keys (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(120) NOT NULL,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    idempotency_key VARCHAR(200) NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    reservation_id BIGINT NOT NULL REFERENCES reservations(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_idempotency_user_show_key UNIQUE(user_id, show_id, idempotency_key)
);
