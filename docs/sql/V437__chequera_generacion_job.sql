-- Generación masiva de borradores sobre las chequeras de una facultad y sede (previa al envío).
-- Base: tesium. Idempotente (CREATE TABLE IF NOT EXISTS). Fechas de negocio: DATETIME(6).
--
-- Etapa BORRADORES: por cada serie del padrón de altas crea (o reutiliza) un borrador de emisión.
--   No escribe en chequera_*.
-- Etapa EMISION: emite los borradores (actualiza chequera_serie/cuota/totales/alternativas/débitos).
-- Cada tarea es su propia transacción; el front consulta el avance por HTTP.

CREATE TABLE IF NOT EXISTS chequera_generacion_job (
    job_id              BIGINT        NOT NULL AUTO_INCREMENT,
    etapa               VARCHAR(20)   NOT NULL,
    job_origen_id       BIGINT        NULL,
    lectivo_id          INT           NOT NULL,
    facultad_id         INT           NOT NULL,
    geografica_id       INT           NOT NULL,
    clase_chequera_id   INT           NULL,
    tipo_chequera_id    INT           NULL,
    solo_no_enviadas    TINYINT(1)    NOT NULL DEFAULT 1,
    forzar_deuda        TINYINT(1)    NOT NULL DEFAULT 0,
    motivo_override     VARCHAR(255)  NULL,
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
    KEY idx_chequera_generacion_job_estado (estado),
    KEY idx_chequera_generacion_job_origen (job_origen_id),
    KEY idx_chequera_generacion_job_filtro (lectivo_id, facultad_id, geografica_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chequera_generacion_job_tarea (
    tarea_id            BIGINT        NOT NULL AUTO_INCREMENT,
    job_id              BIGINT        NOT NULL,
    chequera_id         BIGINT        NULL,
    persona_id          DECIMAL(19,0) NOT NULL,
    documento_id        INT           NOT NULL,
    facultad_id         INT           NOT NULL,
    tipo_chequera_id    INT           NOT NULL,
    chequera_serie_id   BIGINT        NULL,
    curso_id            INT           NULL,
    persona_nombre      VARCHAR(120)  NULL,
    borrador_id         BIGINT        NULL,
    estado              VARCHAR(30)   NOT NULL DEFAULT 'PENDIENTE',
    codigo              VARCHAR(60)   NULL,
    mensaje             VARCHAR(500)  NULL,
    finalizado          DATETIME(6)   NULL,
    PRIMARY KEY (tarea_id),
    UNIQUE KEY uk_chequera_generacion_tarea (job_id, chequera_id),
    UNIQUE KEY uk_chequera_generacion_tarea_borrador (job_id, borrador_id),
    KEY idx_chequera_generacion_tarea_estado (job_id, estado),
    CONSTRAINT fk_chequera_generacion_tarea_job
        FOREIGN KEY (job_id) REFERENCES chequera_generacion_job (job_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
