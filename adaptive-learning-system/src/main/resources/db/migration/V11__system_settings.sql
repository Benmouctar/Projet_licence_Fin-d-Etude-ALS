-- V11__system_settings.sql
-- Stores global application settings (e.g. AI toggle)

CREATE TABLE IF NOT EXISTS system_settings (
    setting_key   VARCHAR(255)  NOT NULL PRIMARY KEY,
    setting_value VARCHAR(255)  NOT NULL,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
