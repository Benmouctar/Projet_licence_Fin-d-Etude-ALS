-- V11__gemini_file_api.sql
-- Adds Google Gemini File API tracking alongside existing vector pipeline.
-- The legacy Qdrant pipeline continues to work; these columns are nullable.
-- Uses conditional DDL so this script is safe to apply multiple times.

-- Track hosted file URIs from the Gemini Files API per module
CREATE TABLE IF NOT EXISTS gemini_module_files (
    id                  VARCHAR(36)     NOT NULL PRIMARY KEY,
    module_id           VARCHAR(36)     NOT NULL UNIQUE,
    course_id           VARCHAR(36)     NOT NULL,
    google_file_uri     VARCHAR(500)    NOT NULL,
    display_name        VARCHAR(500),
    mime_type           VARCHAR(100),
    size_bytes          BIGINT,
    expires_at          TIMESTAMP       NULL,
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT fk_gmf_module
        FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

-- Idempotent index creation (MySQL does not support CREATE INDEX IF NOT EXISTS,
-- so we use a stored procedure trick to skip if already present)
DROP PROCEDURE IF EXISTS add_idx_if_missing;
DELIMITER $$
CREATE PROCEDURE add_idx_if_missing()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name   = 'gemini_module_files'
          AND index_name   = 'idx_gmf_module_id'
    ) THEN
        CREATE INDEX idx_gmf_module_id  ON gemini_module_files (module_id);
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name   = 'gemini_module_files'
          AND index_name   = 'idx_gmf_course_id'
    ) THEN
        CREATE INDEX idx_gmf_course_id  ON gemini_module_files (course_id);
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name   = 'gemini_module_files'
          AND index_name   = 'idx_gmf_active'
    ) THEN
        CREATE INDEX idx_gmf_active     ON gemini_module_files (active);
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name   = 'gemini_module_files'
          AND index_name   = 'idx_gmf_expires_at'
    ) THEN
        CREATE INDEX idx_gmf_expires_at ON gemini_module_files (expires_at);
    END IF;
END$$
DELIMITER ;
CALL add_idx_if_missing();
DROP PROCEDURE IF EXISTS add_idx_if_missing;

-- Idempotent column additions on messages table
DROP PROCEDURE IF EXISTS add_msg_cols_if_missing;
DELIMITER $$
CREATE PROCEDURE add_msg_cols_if_missing()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name   = 'messages'
          AND column_name  = 'gemini_file_uri'
    ) THEN
        ALTER TABLE messages
            ADD COLUMN gemini_file_uri VARCHAR(500) NULL
            COMMENT 'Google file URI used for retrieval, if Gemini pipeline was active';
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name   = 'messages'
          AND column_name  = 'rag_pipeline_used'
    ) THEN
        ALTER TABLE messages
            ADD COLUMN rag_pipeline_used VARCHAR(20) NOT NULL DEFAULT 'legacy'
            COMMENT 'Which RAG pipeline generated this response: legacy | gemini';
    END IF;
END$$
DELIMITER ;
CALL add_msg_cols_if_missing();
DROP PROCEDURE IF EXISTS add_msg_cols_if_missing;
