-- Contexto independiente de autorizantes de envío.
--
-- Un autorizante (usuario) puede cubrir varias dependencias. La relación vive en su propia
-- tabla puente y ya no se administra desde el slice del pedido de compra.
--
-- Base: tesium. Idempotente. Copia los datos de la tabla anterior si existe y no la elimina
-- (se conserva como respaldo durante una release).
--
-- Orden de despliegue: ejecutar este script antes de desplegar core-service.

CREATE TABLE IF NOT EXISTS `compra_autorizante_envio_dependencia` (
  `usuario_id`     int(4)      NOT NULL,
  `dependencia_id` smallint(2) NOT NULL,
  `created`        datetime    DEFAULT NULL,
  `updated`        timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`usuario_id`, `dependencia_id`),
  KEY `ix_caed_dependencia` (`dependencia_id`),
  CONSTRAINT `caed_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`user_id`),
  CONSTRAINT `caed_ibfk_2` FOREIGN KEY (`dependencia_id`) REFERENCES `dependencia` (`dependencia_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migración desde `compra_pedido_autorizante_dependencia` (tabla anterior).
SET @tiene_tabla_anterior := (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'compra_pedido_autorizante_dependencia'
);
SET @migrar := IF(@tiene_tabla_anterior > 0,
    'INSERT IGNORE INTO compra_autorizante_envio_dependencia (usuario_id, dependencia_id, created, updated)
     SELECT autorizante_id, dependencia_id, created, updated
     FROM compra_pedido_autorizante_dependencia',
    'SELECT 1');
PREPARE stmt FROM @migrar;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Clave de permiso para la pantalla de administración.
-- La asignación a roles/usuarios de compras es administración de datos y no se versiona acá.
INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES ('compras.autorizantes.configurar',
        'Administrar las dependencias habilitadas de los autorizantes de envío',
        'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = 'Administrar las dependencias habilitadas de los autorizantes de envío',
    modulo = 'compras',
    activo = 1,
    updated = NOW(6);
