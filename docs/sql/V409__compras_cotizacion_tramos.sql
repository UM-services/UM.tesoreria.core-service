-- Cotizaciones mínimas por monto: CONFIGURACIÓN de tramos (múltiplos de la referencia del ejercicio).
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Etapa de CONFIGURACIÓN. La resolución del mínimo por pedido y la gestión de cotizaciones
-- (carga por proveedor, comparación, recomendación) son etapas posteriores: NO tocan este script.
--
-- 1) Tabla de tramos: cada fila fija un tope y la cantidad mínima de cotizaciones.
--    El tope se expresa en MÚLTIPLOS de `compra_referencia` (valor por ejercicio, V407), de modo
--    que la regla se ajusta sola cuando cambia la referencia. `tope_multiplico` NULL = último
--    tramo (sin tope).
-- 2) Semilla: los tres tramos del prototipo (`superprogramawebdecompras/compras.html`):
--    <= 2R -> 1, <= 6R -> 2, > 6R -> 3. Editable desde la pantalla de administración.
-- 3) Clave de permiso de la etapa: compras.cotizaciones.configurar.
-- 4) Columna `compra_pedido.cantidad_cotizaciones_minima` (snapshot del mínimo por pedido;
--    se completa en la etapa de aplicación). Idempotente.
--
-- La referencia por ejercicio NO se duplica acá: la provee `compra_referencia` (V407).
-- La integridad de los tramos (topes ascendentes, un solo tramo abierto y al final, sin
-- superposición) la valida la aplicación, no el motor.

CREATE TABLE IF NOT EXISTS `compra_cotizacion_tramo` (
  `cotizacion_tramo_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre`              varchar(80)  DEFAULT NULL,
  `tope_multiplico`     int          DEFAULT NULL,
  `cantidad_minima`     int          NOT NULL,
  `activo`              tinyint      NOT NULL DEFAULT 1,
  `created`             datetime     DEFAULT NULL,
  `updated`             timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cotizacion_tramo_id`),
  UNIQUE KEY `uk_compra_cotizacion_tramo_tope` (`tope_multiplico`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Semilla inicial: sólo si la tabla está vacía (no pisa una configuración ya cargada).
INSERT INTO `compra_cotizacion_tramo`
  (`nombre`, `tope_multiplico`, `cantidad_minima`, `activo`, `created`, `updated`)
SELECT nombre, tope_multiplico, cantidad_minima, activo, created, updated
FROM (
  SELECT 'Hasta 2 referencias' AS nombre, 2    AS tope_multiplico, 1 AS cantidad_minima, 1 AS activo, NOW(6) AS created, NOW(6) AS updated
  UNION ALL SELECT 'Hasta 6 referencias', 6,    2, 1, NOW(6), NOW(6)
  UNION ALL SELECT 'Sin tope',            NULL, 3, 1, NOW(6), NOW(6)
) AS seed
WHERE NOT EXISTS (
  SELECT 1 FROM (SELECT `cotizacion_tramo_id` FROM `compra_cotizacion_tramo`) AS existing
);

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.cotizaciones.configurar', 'Configurar la cantidad mínima de cotizaciones por monto (tramos)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);

-- Snapshot del mínimo de cotizaciones por pedido (se completa en la etapa de aplicación).
-- Idempotente: agrega la columna sólo si no existe.
SET @existe_columna := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'compra_pedido'
    AND COLUMN_NAME = 'cantidad_cotizaciones_minima'
);
SET @ddl := IF(@existe_columna = 0,
  'ALTER TABLE `compra_pedido` ADD COLUMN `cantidad_cotizaciones_minima` int DEFAULT NULL AFTER `monto_estimado`',
  'DO 0');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
