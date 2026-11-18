-- Migración de la emisión individual de chequeras (frmImpChequera de tesoreria.vb6)
-- Borrador persistido: permite editar la chequera antes de emitirla y repetir
-- la entrega (PDF / correo) sin volver a emitir.
-- Fechas: DATETIME(6) según convención del proyecto.
--
-- Nombres: ninguna de las dos tablas lleva prefijo en sus columnas. Las PK se llaman
-- `chequera_borrador_id` y `chequera_borrador_cuota_id`; los ordinales de vencimientos e
-- importes van al final (`vencimiento_1`, `importe_original_1`, `importe_borrador_1`).

CREATE TABLE IF NOT EXISTS chequera_borrador (
    chequera_borrador_id BIGINT NOT NULL AUTO_INCREMENT,
    estado VARCHAR(16) NOT NULL DEFAULT 'ABIERTO',
    persona_id DECIMAL(19, 0) NOT NULL,
    documento_id INT NOT NULL,
    facultad_id INT NOT NULL,
    lectivo_id INT NOT NULL,
    tipo_chequera_id INT NOT NULL,
    arancel_tipo_id INT NOT NULL DEFAULT 0,
    alternativa_id INT NULL,
    curso_id INT NULL,
    chequera_serie_id BIGINT NULL,
    fecha DATETIME(6) NULL,
    hpum TINYINT NOT NULL DEFAULT 0,
    beca_porcentaje DECIMAL(9, 4) NOT NULL DEFAULT 0.0000,
    beca_resolucion VARCHAR(255) NULL,
    beca_fecha DATETIME(6) NULL,
    beca_user_id BIGINT NULL,
    calcula_beca TINYINT NOT NULL DEFAULT 0,
    email_personal VARCHAR(255) NULL,
    email_institucional VARCHAR(255) NULL,
    email_copia VARCHAR(255) NULL,
    incluye_barras TINYINT NOT NULL DEFAULT 1,
    copia_facultad TINYINT NOT NULL DEFAULT 0,
    chequera_id BIGINT NULL,
    chequera_serie_id_emitida BIGINT NULL,
    auditoria_id VARCHAR(36) NULL,
    clave_idempotencia VARCHAR(64) NULL,
    usuario_id VARCHAR(64) NULL,
    version INT NOT NULL DEFAULT 0,
    created DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated DATETIME(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    emitido_at DATETIME(6) NULL,
    PRIMARY KEY (chequera_borrador_id),
    UNIQUE KEY uk_chequera_borrador_idempotencia (clave_idempotencia),
    KEY ix_chequera_borrador_usuario_estado (usuario_id, estado),
    KEY ix_chequera_borrador_persona (persona_id, documento_id, lectivo_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chequera_borrador_cuota (
    chequera_borrador_cuota_id BIGINT NOT NULL AUTO_INCREMENT,
    borrador_id BIGINT NOT NULL,
    chequera_cuota_id BIGINT NULL,
    producto_id INT NOT NULL,
    alternativa_id INT NOT NULL,
    cuota_id INT NOT NULL,
    mes INT NULL,
    anio INT NULL,
    vencimiento_1 DATETIME(6) NULL,
    vencimiento_2 DATETIME(6) NULL,
    vencimiento_3 DATETIME(6) NULL,
    importe_original_1 DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    importe_original_2 DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    importe_original_3 DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    importe_borrador_1 DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    importe_borrador_2 DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    importe_borrador_3 DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    arancel_tipo_id INT NULL,
    tramo_id INT NULL,
    descuento_pt DECIMAL(9, 4) NOT NULL DEFAULT 0.0000,
    pagado TINYINT NOT NULL DEFAULT 0,
    baja TINYINT NOT NULL DEFAULT 0,
    manual TINYINT NOT NULL DEFAULT 0,
    compensada TINYINT NOT NULL DEFAULT 0,
    modificado TINYINT NOT NULL DEFAULT 0,
    bloqueado TINYINT NOT NULL DEFAULT 0,
    created DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated DATETIME(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (chequera_borrador_cuota_id),
    UNIQUE KEY uk_chequera_borrador_cuota (borrador_id, producto_id, alternativa_id, cuota_id),
    CONSTRAINT fk_chequera_borrador_cuota_borrador
        FOREIGN KEY (borrador_id) REFERENCES chequera_borrador (chequera_borrador_id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
