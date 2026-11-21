-- Envío masivo de chequeras por facultad y sede (frmCheqMail de prjChequera).
-- Base: tesium. Idempotente (CREATE TABLE IF NOT EXISTS). Fechas de negocio: DATETIME(6).
--
-- Un job = una operación masiva sobre el padrón de altas (lectivo + facultad + sede):
--   CAPTURAR_CORREOS  -> captura/actualiza los correos de cada domicilio
--   EXTENDER_DEBITOS  -> extiende los débitos hasta la última cuota
--   MAILING           -> envía la chequera o, si la persona tiene más de N cuotas impagas
--                        del lectivo, la notificación de deuda (retenida = 1)
-- Cada tarea es su propia transacción; el front consulta el avance por HTTP y el sender
-- confirma el envío real por notificacion-resultado (CHEQUERA/<chequeraId>).

CREATE TABLE IF NOT EXISTS chequera_envio_job (
    job_id                BIGINT       NOT NULL AUTO_INCREMENT,
    operacion             VARCHAR(20)  NOT NULL,
    lectivo_id            INT          NOT NULL,
    facultad_id           INT          NOT NULL,
    geografica_id         INT          NOT NULL,
    solo_no_enviadas      TINYINT(1)   NOT NULL DEFAULT 0,
    codigo_barras         TINYINT(1)   NOT NULL DEFAULT 0,
    umbral_cuotas_deuda   INT          NOT NULL DEFAULT 3,
    estado                VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    total_tareas          INT          NOT NULL DEFAULT 0,
    tareas_ok             INT          NOT NULL DEFAULT 0,
    tareas_omitidas       INT          NOT NULL DEFAULT 0,
    tareas_error          INT          NOT NULL DEFAULT 0,
    tareas_pendientes     INT          NOT NULL DEFAULT 0,
    usuario_id            BIGINT       NULL,
    usuario_login         VARCHAR(50)  NULL,
    heartbeat             DATETIME(6)  NULL,
    iniciado              DATETIME(6)  NULL,
    finalizado            DATETIME(6)  NULL,
    mensaje               VARCHAR(500) NULL,
    ultima_chequera_id    BIGINT       NULL,
    created               DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated               DATETIME(6)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (job_id),
    KEY idx_chequera_envio_job_estado (estado),
    KEY idx_chequera_envio_job_filtro (lectivo_id, facultad_id, geografica_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chequera_envio_job_tarea (
    tarea_id            BIGINT        NOT NULL AUTO_INCREMENT,
    job_id              BIGINT        NOT NULL,
    chequera_id         BIGINT        NOT NULL,
    persona_id          DECIMAL(15,0) NOT NULL,
    documento_id        INT           NOT NULL,
    facultad_id         INT           NOT NULL,
    tipo_chequera_id    INT           NOT NULL,
    chequera_serie_id   BIGINT        NOT NULL,
    persona_nombre      VARCHAR(120)  NULL,
    estado              VARCHAR(30)   NOT NULL DEFAULT 'PENDIENTE',
    codigo              VARCHAR(60)   NULL,
    mensaje             VARCHAR(500)  NULL,
    detalle             VARCHAR(600)  NULL,
    retenida            TINYINT(1)    NULL,
    enviado             TINYINT(1)    NULL,
    finalizado          DATETIME(6)   NULL,
    PRIMARY KEY (tarea_id),
    UNIQUE KEY uk_chequera_envio_tarea (job_id, chequera_id),
    KEY idx_chequera_envio_tarea_chequera (chequera_id),
    KEY idx_chequera_envio_tarea_estado (job_id, estado),
    CONSTRAINT fk_chequera_envio_tarea_job
        FOREIGN KEY (job_id) REFERENCES chequera_envio_job (job_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
