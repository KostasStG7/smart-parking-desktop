-- Initialize a fresh schema. Existing data is not deleted by this script.
CREATE DATABASE IF NOT EXISTS smart_parking_db;
USE smart_parking_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE parking_spots (
    spot_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    address VARCHAR(150) NOT NULL,
    area VARCHAR(100) NOT NULL,
    description TEXT,
    vehicle_type VARCHAR(30) NOT NULL,

    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_parking_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE TABLE availabilities (
    availability_id INT AUTO_INCREMENT PRIMARY KEY,
    spot_id INT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

    CONSTRAINT fk_availability_spot
        FOREIGN KEY (spot_id)
        REFERENCES parking_spots(spot_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_availability_time
        CHECK (end_datetime > start_datetime)
);

CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    spot_id INT NOT NULL,
    driver_id INT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED',

    CONSTRAINT fk_reservation_spot
        FOREIGN KEY (spot_id)
        REFERENCES parking_spots(spot_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservation_driver
        FOREIGN KEY (driver_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_reservation_time
        CHECK (end_datetime > start_datetime)
);

CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

ALTER TABLE parking_spots
ADD COLUMN pricing_enabled BOOLEAN NOT NULL DEFAULT FALSE AFTER vehicle_type,
ADD COLUMN price_per_hour DECIMAL(6,2) NULL AFTER pricing_enabled,
ADD COLUMN emergency_booking_enabled BOOLEAN NOT NULL DEFAULT FALSE AFTER price_per_hour;

ALTER TABLE reservations
ADD COLUMN calculated_price DECIMAL(8,2) NOT NULL DEFAULT 0.00 AFTER status,
ADD COLUMN payment_status VARCHAR(30) NOT NULL DEFAULT 'NOT_REQUIRED' AFTER calculated_price,
ADD COLUMN payment_deadline DATETIME NULL AFTER payment_status,
ADD COLUMN pricing_accepted BOOLEAN NOT NULL DEFAULT FALSE AFTER payment_deadline,
ADD COLUMN booking_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL' AFTER pricing_accepted;