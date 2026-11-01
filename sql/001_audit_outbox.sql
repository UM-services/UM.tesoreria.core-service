-- Outbox transaccional de auditoría de tesoreria-core.
-- La aplicación NO crea ni migra el esquema (ddl-auto: none).
-- Va en la base de negocio (tesium) porque comparte transacción con los cambios.
CREATE TABLE IF NOT EXISTS audit_outbox (
    outbox_id       BIGINT       NOT NULL AUTO_INCREMENT,
    event_id        CHAR(36)     NOT NULL,
    payload         JSON         NOT NULL,
    status          VARCHAR(12)  NOT NULL DEFAULT 'PENDING',
    attempts        INT          NOT NULL DEFAULT 0,
    next_attempt_at DATETIME(6)  NOT NULL,
    claim_token     CHAR(36)     NULL,
    created         DATETIME(6)  NOT NULL,
    sent_at         DATETIME(6)  NULL,
    last_error      VARCHAR(500) NULL,
    PRIMARY KEY (outbox_id),
    UNIQUE KEY uk_audit_outbox_event (event_id),
    KEY idx_audit_outbox_pending (status, next_attempt_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
