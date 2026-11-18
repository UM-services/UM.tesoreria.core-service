-- Chequeras Incompletas (frmIncompletas de prjChequera): job masivo de dos etapas.
-- Base: tesium. Idempotente (CREATE TABLE IF NOT EXISTS). Fechas: DATETIME(6).
--
-- Etapa BORRADORES: por cada serie incompleta crea (o reutiliza) un borrador de emisión que
-- completa las cuotas faltantes preservando la beca de la serie. No escribe en chequera_*.
-- Etapa EMISION: emite los borradores de la etapa anterior (emitir + extendDebito).
-- Cada tarea es su propia transacción; el front consulta el avance por HTTP.

CREATE TABLE IF NOT EXISTS chequera_incompleta_job (
    job_id              BIGINT        NOT NULL AUTO_INCREMENT,
    etapa               VARCHAR(20)   NOT NULL,
    job_origen_id       BIGINT        NULL,
    lectivo_id          INT           NOT NULL,
    facultad_id         INT           NOT NULL,
    geografica_id       INT           NOT NULL,
    clase_chequera_id   INT           NOT NULL,
    solo_no_enviadas    TINYINT(1)    NOT NULL DEFAULT 0,
    estado              VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    total_tareas        INT           NOT NULL DEFAULT 0,
    tareas_ok           INT           NOT NULL DEFAULT 0,
    tareas_omitidas     INT           NOT NULL DEFAULT 0,
    tareas_error        INT           NOT NULL DEFAULT 0,
    tareas_pendientes   INT           NOT NULL DEFAULT 0,
    usuario_id          BIGINT        NULL,
    usuario_login       VARCHAR(50)   NULL,
    heartbeat           DATETIME(6)   NULL,
    iniciado            DATETIME(6)   NULL,
    finalizado          DATETIME(6)   NULL,
    mensaje             VARCHAR(500)  NULL,
    ultima_chequera_id  BIGINT        NULL,
    created             DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated             DATETIME(6)   NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (job_id),
    KEY idx_chequera_incompleta_job_estado (estado),
    KEY idx_chequera_incompleta_job_origen (job_origen_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chequera_incompleta_job_tarea (
    tarea_id            BIGINT        NOT NULL AUTO_INCREMENT,
    job_id              BIGINT        NOT NULL,
    chequera_id         BIGINT        NOT NULL,
    persona_id          DECIMAL(15,0) NOT NULL,
    documento_id        INT           NOT NULL,
    facultad_id         INT           NOT NULL,
    tipo_chequera_id    INT           NOT NULL,
    chequera_serie_id   BIGINT        NOT NULL,
    persona_nombre      VARCHAR(120)  NULL,
    borrador_id         BIGINT        NULL,
    estado              VARCHAR(30)   NOT NULL DEFAULT 'PENDIENTE',
    codigo              VARCHAR(60)   NULL,
    mensaje             VARCHAR(500)  NULL,
    finalizado          DATETIME(6)   NULL,
    PRIMARY KEY (tarea_id),
    UNIQUE KEY uk_chequera_incompleta_tarea (job_id, chequera_id),
    CONSTRAINT fk_chequera_incompleta_tarea_job
        FOREIGN KEY (job_id) REFERENCES chequera_incompleta_job (job_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
