-- Etapa 9 — Recepción y conformidad de una Orden de Compra.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Modelo (decisiones §18 de PLANIFICACION_COMPRAS.md):
--   * compra_recepcion      = una entrega / remito contra una Orden de Compra: fecha, remito,
--                             resultado (conforme / con observaciones / no conforme), intervención
--                             de Patrimonio y observaciones. Numeración propia RC-AAAA-NNNNNN.
--   * compra_recepcion_item = detalle: cantidad recibida y resultado por ítem de la OC
--                             (compra_formalizacion_item). El saldo es DERIVADO (contratado −
--                             cubierto): no se persiste.
--
-- Alcance: sólo Orden de Compra (el Contrato sigue fuera, D-FOR-5). Sin efecto contable, sin flujo
-- de devolución y sin alta patrimonial (D-REC-5/6/7). La recepción confirmada es inmutable
-- (no hay endpoint de edición, D-REC-12). El correlativo usa LAST_INSERT_ID, mismo mecanismo que
-- compra_pedido_secuencia / compra_cotizacion_secuencia / compra_formalizacion_secuencia.
-- La integridad de negocio (ítem de la OC, cantidad dentro del saldo, estados) la valida la
-- aplicación; acá sólo se garantizan las claves y vínculos estructurales.

-- Correlativo anual de recepciones (RC-AAAA-NNNNNN).
CREATE TABLE IF NOT EXISTS `compra_recepcion_secuencia` (
  `ejercicio_id`  smallint(2) NOT NULL,
  `anio`          smallint(2) NOT NULL,
  `ultimo_numero` int(4)      NOT NULL,
  `created`       datetime    DEFAULT NULL,
  `updated`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ejercicio_id`),
  CONSTRAINT `compra_recepcion_secuencia_ibfk_1` FOREIGN KEY (`ejercicio_id`) REFERENCES `ejercicios` (`Eje_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Una entrega (un remito) contra una Orden de Compra; puede haber varias por OC (parciales).
CREATE TABLE IF NOT EXISTS `compra_recepcion` (
  `compra_recepcion_id`     bigint        NOT NULL AUTO_INCREMENT,
  `compra_formalizacion_id` bigint        NOT NULL,   -- Orden de Compra de origen
  `numero`                  varchar(16)   DEFAULT NULL, -- RC-AAAA-NNNNNN (reserva al confirmar)
  `ejercicio_id`            smallint(2)   NOT NULL,
  `fecha`                   datetime(6)   NOT NULL,
  `remito`                  varchar(40)   DEFAULT NULL,
  `resultado`               varchar(20)   NOT NULL,   -- CONFORME | CON_OBSERVACIONES | NO_CONFORME
  `patrimonio`              varchar(20)   NOT NULL DEFAULT 'NO_CORRESPONDE', -- INTERVENIDO | NO_CORRESPONDE | PENDIENTE
  `observacion`             varchar(500)  DEFAULT NULL,
  `usuario_id`              int(4)        NOT NULL,   -- quién recepciona
  `created`                 datetime      DEFAULT NULL,
  `updated`                 timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_recepcion_id`),
  UNIQUE KEY `uk_compra_recepcion_numero` (`numero`),
  KEY `ix_compra_recepcion_formalizacion` (`compra_formalizacion_id`),
  KEY `ix_compra_recepcion_consulta` (`ejercicio_id`,`fecha`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `compra_recepcion_ibfk_1` FOREIGN KEY (`compra_formalizacion_id`) REFERENCES `compra_formalizacion` (`compra_formalizacion_id`),
  CONSTRAINT `compra_recepcion_ibfk_2` FOREIGN KEY (`ejercicio_id`)           REFERENCES `ejercicios` (`Eje_ID`),
  CONSTRAINT `compra_recepcion_ibfk_3` FOREIGN KEY (`usuario_id`)             REFERENCES `usuario` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Detalle de la entrega: cantidad recibida y resultado por ítem contratado.
CREATE TABLE IF NOT EXISTS `compra_recepcion_item` (
  `compra_recepcion_item_id`     bigint        NOT NULL AUTO_INCREMENT,
  `compra_recepcion_id`          bigint        NOT NULL,
  `compra_formalizacion_item_id` bigint        NOT NULL,   -- ítem contratado (trazabilidad + saldo)
  `orden`                        smallint(2)   NOT NULL,
  `cantidad_recibida`            decimal(10,2) NOT NULL DEFAULT 0,
  `resultado`                    varchar(20)   NOT NULL,   -- CONFORME | CON_OBSERVACIONES | NO_CONFORME
  `observacion`                  varchar(500)  DEFAULT NULL,
  `created`                      datetime      DEFAULT NULL,
  `updated`                      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_recepcion_item_id`),
  UNIQUE KEY `uk_compra_recepcion_item` (`compra_recepcion_id`,`compra_formalizacion_item_id`),
  KEY `ix_compra_recepcion_item_form_item` (`compra_formalizacion_item_id`),
  CONSTRAINT `compra_recepcion_item_ibfk_1` FOREIGN KEY (`compra_recepcion_id`)          REFERENCES `compra_recepcion` (`compra_recepcion_id`),
  CONSTRAINT `compra_recepcion_item_ibfk_2` FOREIGN KEY (`compra_formalizacion_item_id`) REFERENCES `compra_formalizacion_item` (`compra_formalizacion_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Permiso de la etapa 9. La asignación a roles/usuarios (rol_permiso / usuario_permiso) es
-- administración de datos y no se versiona acá.
INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.recepcionar', 'Registrar la recepción y conformidad de una Orden de Compra (entregas totales o parciales y cierre con motivo)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
