-- Asiento Ingreso (frmAsientoIngreso de prjGestion): job masivo persistido.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente (CREATE TABLE IF NOT EXISTS).
-- Fechas: DATETIME(6) según convención del proyecto.
--
-- El job recorre un rango de fechas × tipos de pago. Cada combinación es una tarea con su propio
-- estado; el worker la procesa en una transacción y el front consulta el avance por HTTP.

CREATE TABLE IF NOT EXISTS contable_asiento_ingreso_job (
    job_id             BIGINT        NOT NULL AUTO_INCREMENT,
    estado             VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_desde        DATETIME(6)   NOT NULL,
    fecha_hasta        DATETIME(6)   NOT NULL,
    total_tareas       INT           NOT NULL DEFAULT 0,
    tareas_ok          INT           NOT NULL DEFAULT 0,
    tareas_error       INT           NOT NULL DEFAULT 0,
    tareas_sin_mov     INT           NOT NULL DEFAULT 0,
    tareas_pendientes  INT           NOT NULL DEFAULT 0,
    usuario_id         BIGINT        NULL,
    heartbeat          DATETIME(6)   NULL,
    iniciado           DATETIME(6)   NULL,
    finalizado         DATETIME(6)   NULL,
    mensaje            VARCHAR(500)  NULL,
    fecha_actual       DATETIME(6)   NULL,
    tipo_pago_actual   INT           NULL,
    created            DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated            DATETIME(6)   NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (job_id),
    KEY idx_asiento_ingreso_job_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS contable_asiento_ingreso_job_tarea (
    tarea_id        BIGINT        NOT NULL AUTO_INCREMENT,
    job_id          BIGINT        NOT NULL,
    fecha_contable  DATETIME(6)   NOT NULL,
    tipo_pago_id    INT           NOT NULL,
    estado          VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    orden_contable  INT           NULL,
    total_debe      DECIMAL(16,2) NULL,
    total_haber     DECIMAL(16,2) NULL,
    faltantes_json  TEXT          NULL,
    error_codigo    VARCHAR(60)   NULL,
    error_mensaje   VARCHAR(500)  NULL,
    iniciado        DATETIME(6)   NULL,
    finalizado      DATETIME(6)   NULL,
    PRIMARY KEY (tarea_id),
    UNIQUE KEY uk_asiento_ingreso_tarea (job_id, fecha_contable, tipo_pago_id),
    CONSTRAINT fk_asiento_ingreso_tarea_job
        FOREIGN KEY (job_id) REFERENCES contable_asiento_ingreso_job (job_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
