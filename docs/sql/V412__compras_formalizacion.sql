-- Etapa 8 — Formalización (Orden de Compra) de una adjudicación.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Modelo (decisiones §17 de PLANIFICACION_COMPRAS.md):
--   * compra_formalizacion      = la Orden de Compra: se emite a partir del acto de adjudicación
--                                 (compra_adjudicacion) y snapshotea proveedor, monto y condiciones.
--                                 Una sola OC por adjudicación (UNIQUE compra_adjudicacion_id).
--   * compra_formalizacion_item = detalle (snapshot): ítems/cantidades copiados del proceso y
--                                 precios/marca de la oferta adjudicada.
--
-- El alcance de esta etapa es SÓLO Orden de Compra: el Contrato queda para más adelante (si entra,
-- es una migración aditiva). El correlativo es OC-AAAA-NNNNNN (mismo mecanismo que
-- compra_pedido_secuencia / compra_cotizacion_secuencia, LAST_INSERT_ID).
-- La integridad de negocio (adjudicación ADJUDICADA, estado del proceso, etc.) la valida la
-- aplicación; acá sólo se garantizan las claves y vínculos estructurales.

-- Correlativo anual de Órdenes de Compra (OC-AAAA-NNNNNN).
CREATE TABLE IF NOT EXISTS `compra_formalizacion_secuencia` (
  `ejercicio_id`  smallint(2) NOT NULL,
  `anio`          smallint(2) NOT NULL,
  `ultimo_numero` int(4)      NOT NULL,
  `created`       datetime    DEFAULT NULL,
  `updated`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ejercicio_id`),
  CONSTRAINT `compra_formalizacion_secuencia_ibfk_1` FOREIGN KEY (`ejercicio_id`) REFERENCES `ejercicios` (`Eje_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La Orden de Compra (una por adjudicación).
CREATE TABLE IF NOT EXISTS `compra_formalizacion` (
  `compra_formalizacion_id` bigint        NOT NULL AUTO_INCREMENT,
  `compra_adjudicacion_id`  bigint        NOT NULL,   -- origen único del instrumento
  `numero`                  varchar(16)   NOT NULL,   -- OC-AAAA-NNNNNN
  `numero_externo`          varchar(40)   DEFAULT NULL, -- nº de la OC firmada fuera del sistema
  `ejercicio_id`            smallint(2)   NOT NULL,
  `fecha`                   datetime(6)   NOT NULL,
  `proveedor_id`            smallint(2)   NOT NULL,   -- = proveedores.Prv_ID (smallint)
  `moneda`                  varchar(10)   DEFAULT NULL,
  `monto`                   decimal(19,2) DEFAULT NULL,
  `condicion_pago`          varchar(100)  DEFAULT NULL,
  `plazo_entrega`           varchar(100)  DEFAULT NULL,
  `garantia`                varchar(100)  DEFAULT NULL,
  `objeto`                  text          DEFAULT NULL,   -- objeto/alcance (HTML del editor)
  `observacion`             varchar(500)  DEFAULT NULL,
  `usuario_id`              int(4)        NOT NULL,       -- compras: quien formaliza
  `created`                 datetime      DEFAULT NULL,
  `updated`                 timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_formalizacion_id`),
  UNIQUE KEY `uk_compra_formalizacion_numero` (`numero`),
  UNIQUE KEY `uk_compra_formalizacion_adjudicacion` (`compra_adjudicacion_id`),
  KEY `ix_compra_formalizacion_consulta` (`ejercicio_id`,`fecha`),
  KEY `ix_compra_formalizacion_proveedor` (`proveedor_id`),
  CONSTRAINT `compra_formalizacion_ibfk_1` FOREIGN KEY (`compra_adjudicacion_id`) REFERENCES `compra_adjudicacion` (`compra_adjudicacion_id`),
  CONSTRAINT `compra_formalizacion_ibfk_2` FOREIGN KEY (`ejercicio_id`)           REFERENCES `ejercicios` (`Eje_ID`),
  CONSTRAINT `compra_formalizacion_ibfk_3` FOREIGN KEY (`proveedor_id`)           REFERENCES `proveedores` (`prv_id`),
  CONSTRAINT `compra_formalizacion_ibfk_4` FOREIGN KEY (`usuario_id`)             REFERENCES `usuario` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Detalle de la OC (snapshot): ítems copiados del proceso y de la oferta adjudicada.
CREATE TABLE IF NOT EXISTS `compra_formalizacion_item` (
  `compra_formalizacion_item_id` bigint        NOT NULL AUTO_INCREMENT,
  `compra_formalizacion_id`      bigint        NOT NULL,
  `compra_cotizacion_item_id`    bigint        DEFAULT NULL,   -- trazabilidad al ítem del proceso
  `compra_pedido_item_id`        int(4)        DEFAULT NULL,   -- trazabilidad al ítem del pedido
  `orden`                        smallint(2)   NOT NULL,
  `cantidad`                     decimal(10,2) NOT NULL,
  `unidad`                       varchar(50)   DEFAULT NULL,
  `descripcion`                  varchar(255)  DEFAULT NULL,
  `especificaciones`             text          DEFAULT NULL,
  `precio_unitario`              decimal(19,2) DEFAULT NULL,
  `subtotal`                     decimal(19,2) DEFAULT NULL,
  `marca`                        varchar(150)  DEFAULT NULL,
  `created`                      datetime      DEFAULT NULL,
  `updated`                      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_formalizacion_item_id`),
  KEY `ix_compra_formalizacion_item_form` (`compra_formalizacion_id`),
  CONSTRAINT `compra_formalizacion_item_ibfk_1` FOREIGN KEY (`compra_formalizacion_id`)   REFERENCES `compra_formalizacion` (`compra_formalizacion_id`),
  CONSTRAINT `compra_formalizacion_item_ibfk_2` FOREIGN KEY (`compra_cotizacion_item_id`) REFERENCES `compra_cotizacion_item` (`compra_cotizacion_item_id`),
  CONSTRAINT `compra_formalizacion_item_ibfk_3` FOREIGN KEY (`compra_pedido_item_id`)     REFERENCES `compra_pedido_item` (`compra_pedido_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Permiso de la etapa 8. La asignación a roles/usuarios (rol_permiso / usuario_permiso) es
-- administración de datos y no se versiona acá.
INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.formalizar', 'Emitir la Orden de Compra de una adjudicación (dpto. de compras)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
