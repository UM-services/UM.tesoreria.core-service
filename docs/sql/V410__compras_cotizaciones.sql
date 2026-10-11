-- Etapa 6 — Pedidos de cotización (solicitud de presupuestos), recepción y recomendación.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Modelo (decisiones §14 de PLANIFICACION_COMPRAS.md):
--   * compra_cotizacion                = proceso de cotización (agrupador): ítems combinados de
--                                        varios pedidos, mínimo exigido y recomendación.
--   * compra_cotizacion_item           = ítems combinados; vínculo (trazabilidad) al ítem de
--                                        origen (compra_pedido_item). El pedido entra completo y
--                                        un ítem pertenece a un solo proceso vigente (lo valida
--                                        la aplicación).
--   * compra_cotizacion_proveedor      = UN pedido de cotización POR PROVEEDOR: documento enviado
--                                        (email snapshot, fecha_envio) y cabecera de la respuesta.
--   * compra_cotizacion_proveedor_item = detalle de la respuesta: precio por ítem del proceso.
--
-- El correlativo del proceso es SC-AAAA-NNNNNN (mismo mecanismo que compra_pedido_secuencia, §5).
-- El envío real de correo NO se implementa en esta etapa: se modela el envío previsto.
-- La integridad de negocio (exclusividad del ítem, mínimo alcanzado, recomendación única, etc.)
-- la valida la aplicación; acá sólo se garantizan las claves y vínculos estructurales.

-- Correlativo anual de procesos de cotización (SC-AAAA-NNNNNN).
CREATE TABLE IF NOT EXISTS `compra_cotizacion_secuencia` (
  `ejercicio_id`  smallint(2) NOT NULL,
  `anio`          smallint(2) NOT NULL,
  `ultimo_numero` int(4)      NOT NULL,
  `created`       datetime    DEFAULT NULL,
  `updated`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ejercicio_id`),
  CONSTRAINT `compra_cotizacion_secuencia_ibfk_1` FOREIGN KEY (`ejercicio_id`) REFERENCES `ejercicios` (`Eje_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Proceso de cotización: agrupador de ítems combinados y de los pedidos por proveedor.
CREATE TABLE IF NOT EXISTS `compra_cotizacion` (
  `compra_cotizacion_id`     bigint        NOT NULL AUTO_INCREMENT,
  `numero`                   varchar(16)   DEFAULT NULL,
  `ejercicio_id`             smallint(2)   NOT NULL,
  `fecha`                    datetime(6)   DEFAULT NULL,
  `estado`                   varchar(30)   NOT NULL,
  `observacion`              varchar(500)  DEFAULT NULL,
  `fecha_limite_respuesta`   datetime(6)   DEFAULT NULL,
  `monto_estimado`           decimal(19,2) DEFAULT NULL,
  `cantidad_minima`          int           DEFAULT NULL,
  `excepcion_motivo`         varchar(500)  DEFAULT NULL,
  `fecha_envio`              datetime(6)   DEFAULT NULL,
  `proveedor_recomendado_id` bigint        DEFAULT NULL,
  `fundamento_recomendacion` varchar(1000) DEFAULT NULL,
  `fecha_recomendacion`      datetime(6)   DEFAULT NULL,
  `usuario_id`               int(4)        DEFAULT NULL,
  `created`                  datetime      DEFAULT NULL,
  `updated`                  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_cotizacion_id`),
  UNIQUE KEY `uk_compra_cotizacion_numero` (`numero`),
  KEY `ix_compra_cotizacion_consulta` (`ejercicio_id`,`estado`,`fecha`),
  KEY `ix_compra_cotizacion_usuario` (`usuario_id`),
  KEY `ix_compra_cotizacion_recomendado` (`proveedor_recomendado_id`),
  CONSTRAINT `compra_cotizacion_ibfk_1` FOREIGN KEY (`ejercicio_id`) REFERENCES `ejercicios` (`Eje_ID`),
  CONSTRAINT `compra_cotizacion_ibfk_2` FOREIGN KEY (`usuario_id`)   REFERENCES `usuario` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Ítems combinados: el vínculo con el ítem de origen da la trazabilidad al pedido.
CREATE TABLE IF NOT EXISTS `compra_cotizacion_item` (
  `compra_cotizacion_item_id` bigint        NOT NULL AUTO_INCREMENT,
  `compra_cotizacion_id`      bigint        NOT NULL,
  `compra_pedido_item_id`     int(4)        NOT NULL,
  `orden`                     smallint(2)   NOT NULL,
  `cantidad`                  decimal(10,2) NOT NULL,
  `created`                   datetime      DEFAULT NULL,
  `updated`                   timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_cotizacion_item_id`),
  UNIQUE KEY `uk_compra_cotizacion_item` (`compra_cotizacion_id`,`compra_pedido_item_id`),
  KEY `ix_compra_cotizacion_item_origen` (`compra_pedido_item_id`),
  CONSTRAINT `compra_cotizacion_item_ibfk_1` FOREIGN KEY (`compra_cotizacion_id`)  REFERENCES `compra_cotizacion` (`compra_cotizacion_id`),
  CONSTRAINT `compra_cotizacion_item_ibfk_2` FOREIGN KEY (`compra_pedido_item_id`) REFERENCES `compra_pedido_item` (`compra_pedido_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Pedido de cotización por proveedor: documento enviado + cabecera de la respuesta.
CREATE TABLE IF NOT EXISTS `compra_cotizacion_proveedor` (
  `compra_cotizacion_proveedor_id` bigint        NOT NULL AUTO_INCREMENT,
  `compra_cotizacion_id`           bigint        NOT NULL,
  `proveedor_id`                   smallint(2)   NOT NULL,   -- = proveedores.Prv_ID (smallint)
  `email`                          varchar(150)  DEFAULT NULL,
  `estado`                         varchar(20)   NOT NULL DEFAULT 'PENDIENTE',
  `fecha_envio`                    datetime(6)   DEFAULT NULL,
  `observacion`                    varchar(500)  DEFAULT NULL,
  `fecha_respuesta`                datetime(6)   DEFAULT NULL,
  `moneda`                         varchar(10)   DEFAULT NULL,
  `total`                          decimal(19,2) DEFAULT NULL,
  `plazo_entrega`                  varchar(100)  DEFAULT NULL,
  `condicion_pago`                 varchar(100)  DEFAULT NULL,
  `garantia`                       varchar(100)  DEFAULT NULL,
  `validez_oferta`                 varchar(100)  DEFAULT NULL,
  `created`                        datetime      DEFAULT NULL,
  `updated`                        timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_cotizacion_proveedor_id`),
  UNIQUE KEY `uk_compra_cotizacion_proveedor` (`compra_cotizacion_id`,`proveedor_id`),
  KEY `ix_compra_cotizacion_proveedor_prov` (`proveedor_id`),
  CONSTRAINT `compra_cotizacion_proveedor_ibfk_1` FOREIGN KEY (`compra_cotizacion_id`) REFERENCES `compra_cotizacion` (`compra_cotizacion_id`),
  CONSTRAINT `compra_cotizacion_proveedor_ibfk_2` FOREIGN KEY (`proveedor_id`)        REFERENCES `proveedores` (`prv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Detalle de la respuesta: precio del proveedor por ítem del proceso.
CREATE TABLE IF NOT EXISTS `compra_cotizacion_proveedor_item` (
  `compra_cotizacion_proveedor_item_id` bigint        NOT NULL AUTO_INCREMENT,
  `compra_cotizacion_proveedor_id`      bigint        NOT NULL,
  `compra_cotizacion_item_id`           bigint        NOT NULL,
  `cotiza`                              tinyint       NOT NULL DEFAULT 1,
  `precio_unitario`                     decimal(19,2) DEFAULT NULL,
  `subtotal`                            decimal(19,2) DEFAULT NULL,
  `marca`                               varchar(150)  DEFAULT NULL,
  `observacion`                         varchar(500)  DEFAULT NULL,
  `created`                             datetime      DEFAULT NULL,
  `updated`                             timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_cotizacion_proveedor_item_id`),
  UNIQUE KEY `uk_compra_cotizacion_prov_item` (`compra_cotizacion_proveedor_id`,`compra_cotizacion_item_id`),
  KEY `ix_compra_cotizacion_prov_item_item` (`compra_cotizacion_item_id`),
  CONSTRAINT `compra_cotizacion_prov_item_ibfk_1` FOREIGN KEY (`compra_cotizacion_proveedor_id`) REFERENCES `compra_cotizacion_proveedor` (`compra_cotizacion_proveedor_id`),
  CONSTRAINT `compra_cotizacion_prov_item_ibfk_2` FOREIGN KEY (`compra_cotizacion_item_id`)      REFERENCES `compra_cotizacion_item` (`compra_cotizacion_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- FK de la recomendación: referencia cruzada proceso → documento de proveedor elegido.
-- Se agrega aparte para evitar el orden circular de creación entre las dos tablas; idempotente.
SET @existe_fk_recomendado := (
  SELECT COUNT(*) FROM information_schema.REFERENTIAL_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND CONSTRAINT_NAME = 'compra_cotizacion_ibfk_3'
);
SET @ddl_fk_recomendado := IF(@existe_fk_recomendado = 0,
  'ALTER TABLE `compra_cotizacion` ADD CONSTRAINT `compra_cotizacion_ibfk_3` FOREIGN KEY (`proveedor_recomendado_id`) REFERENCES `compra_cotizacion_proveedor` (`compra_cotizacion_proveedor_id`)',
  'DO 0');
PREPARE stmt FROM @ddl_fk_recomendado;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Permisos de la etapa 6.
--   compras.cotizaciones.gestionar: armar/editar/enviar/anular procesos, cargar respuestas y recomendar.
--   compras.cotizaciones.excepcion: solicitar menos cotizaciones que el mínimo, con motivo.
-- La asignación a roles/usuarios (rol_permiso / usuario_permiso) es administración de datos.
INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.cotizaciones.gestionar', 'Gestionar procesos de cotización: armar, enviar, cargar respuestas y recomendar', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.cotizaciones.excepcion', 'Solicitar menos cotizaciones que el mínimo exigido, con motivo', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
