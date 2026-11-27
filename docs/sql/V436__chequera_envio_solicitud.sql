-- Ledger idempotente del envío de chequeras.
-- Una solicitud por chequera (clave de chequera_serie): la reentrega de Kafka, un doble clic o un
-- job repetido no vuelven a mandar el correo. Estados: PENDIENTE, ENVIANDO, ENVIADO, FALLIDO,
-- OMITIDA, SIN_CORREOS, SIN_CUOTAS, RETENIDA.
--
-- solicitud_id viaja en el evento send-chequera; chequera_id es la garantía de negocio (única).
-- lock_hasta serializa el reclamo entre instancias del sender; enviado_at registra el envío real.

CREATE TABLE IF NOT EXISTS chequera_envio_solicitud (
    solicitud_id        BINARY(16)   NOT NULL,
    chequera_id         BIGINT       NOT NULL,
    facultad_id         INT          NOT NULL,
    tipo_chequera_id    INT          NOT NULL,
    chequera_serie_id   BIGINT       NOT NULL,
    lectivo_id          INT          NULL,
    job_id              BIGINT       NULL,
    origen              VARCHAR(20)  NOT NULL,
    dedupe_key          VARCHAR(120) NOT NULL,
    estado              VARCHAR(30)  NOT NULL,
    intentos            INT          NOT NULL DEFAULT 0,
    lock_hasta          DATETIME(6)  NULL,
    enviado_at          DATETIME(6)  NULL,
    ultimo_error        VARCHAR(500) NULL,
    created             DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated             DATETIME(6)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (solicitud_id),
    UNIQUE KEY uk_chequera_envio_solicitud_chequera (chequera_id),
    UNIQUE KEY uk_chequera_envio_solicitud_dedupe (dedupe_key),
    KEY idx_chequera_envio_solicitud_estado (estado),
    KEY idx_chequera_envio_solicitud_job (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
