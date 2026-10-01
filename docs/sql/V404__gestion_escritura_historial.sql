-- Issue #404 — Historial transaccional de escrituras (Gestión)
-- Base: tesium. Reproducible con ddl-auto: none.
-- No incluye actor/usuario verificado. Sin vista ni endpoint público.

CREATE TABLE IF NOT EXISTS gestion_escritura_historial (
    escritura_historial_id BIGINT NOT NULL AUTO_INCREMENT,
    fecha                  DATETIME(6) NOT NULL,
    operacion              VARCHAR(16) NOT NULL,
    entidad                VARCHAR(128) NOT NULL,
    entidad_clave          VARCHAR(255) NOT NULL,
    valor_anterior         LONGTEXT NULL,
    valor_nuevo            LONGTEXT NULL,
    PRIMARY KEY (escritura_historial_id),
    KEY idx_geh_entidad_clave (entidad, entidad_clave),
    KEY idx_geh_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
