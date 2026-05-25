-- V11__gemini_file_api.sql (PostgreSQL)
-- Adds Google Gemini File API tracking alongside existing vector pipeline.
-- The legacy Qdrant pipeline continues to work; these columns are nullable.

-- Track hosted file URIs from the Gemini Files API per module
CREATE TABLE IF NOT EXISTS gemini_module_files (
    id                  VARCHAR(36)     NOT NULL PRIMARY KEY,
    module_id           VARCHAR(36)     NOT NULL UNIQUE,
    course_id           VARCHAR(36)     NOT NULL,

    -- The URI returned by Google Files API (e.g. "files/abc123xyz")
    google_file_uri     VARCHAR(500)    NOT NULL,

    -- The display name sent during upload (human-readable for debugging)
    display_name        VARCHAR(500),

    -- MIME type of the uploaded file (e.g. application/pdf, video/mp4)
    mime_type           VARCHAR(100),

    -- File size in bytes (from Google API response)
    size_bytes          BIGINT,

    -- Google's expiry timestamp (Files API files expire after ~48 hours)
    expires_at          TIMESTAMP       NULL,

    -- Whether this file is currently active and usable for RAG
    active              BOOLEAN         NOT NULL DEFAULT TRUE,

    -- Auditable columns
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),

    CONSTRAINT fk_gmf_module
        FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

CREATE INDEX idx_gmf_module_id   ON gemini_module_files (module_id);
CREATE INDEX idx_gmf_course_id   ON gemini_module_files (course_id);
CREATE INDEX idx_gmf_active      ON gemini_module_files (active);
CREATE INDEX idx_gmf_expires_at  ON gemini_module_files (expires_at);

-- Track individual multimodal queries and their retrieved context
ALTER TABLE messages
    ADD COLUMN IF NOT EXISTS gemini_file_uri   VARCHAR(500) NULL,
    ADD COLUMN IF NOT EXISTS rag_pipeline_used VARCHAR(20)  NOT NULL DEFAULT 'legacy';
