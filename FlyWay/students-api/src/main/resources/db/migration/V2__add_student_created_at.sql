-- ==============================================================
-- Flyway V2 : add a creation timestamp without losing existing data
-- Compatible with H2 (local) and PostgreSQL (Azure)
-- ==============================================================

ALTER TABLE student
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
