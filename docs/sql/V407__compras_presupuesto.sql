-- Autorización previa por monto: etapa de autorización del proceso de pedido de presupuesto.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- 1) Tablas de referencia (valor por ejercicio), perfiles de autoridad (múltiplo) y asignación
--    usuario ↔ perfil. El límite efectivo de un usuario es MAX(multiplico) × referencia.
--    multiplico NULL = ilimitado.
-- 2) Claves de permiso de la etapa: compras.estimar (dpto. de compras) y
--    compras.presupuesto.autorizar (autoridad por monto).
-- 3) Migración de estado: ENVIADO pasa a ser EN_REVISION_COMPRAS (el pedido queda a cargo
--    del dpto. de compras para su revisión/estimación).
--
-- La asignación de perfiles a usuarios y el valor de la referencia son administración de datos.

CREATE TABLE IF NOT EXISTS `compra_referencia` (
  `ejercicio_id` smallint(2) NOT NULL,
  `importe`      decimal(19,2) NOT NULL,
  `created`      datetime DEFAULT NULL,
  `updated`      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ejercicio_id`),
  CONSTRAINT `compra_referencia_ibfk_1` FOREIGN KEY (`ejercicio_id`) REFERENCES `ejercicios` (`Eje_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `compra_autoridad_perfil` (
  `autoridad_perfil_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre`              varchar(80) NOT NULL,
  `multiplico`          int DEFAULT NULL,
  `activo`              tinyint NOT NULL DEFAULT 1,
  `created`             datetime DEFAULT NULL,
  `updated`             timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`autoridad_perfil_id`),
  UNIQUE KEY `uk_compra_autoridad_perfil_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `compra_autoridad_usuario` (
  `autoridad_usuario_id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id`           int(4) NOT NULL,
  `autoridad_perfil_id`  bigint NOT NULL,
  `created`              datetime DEFAULT NULL,
  `updated`              timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`autoridad_usuario_id`),
  UNIQUE KEY `uk_compra_autoridad_usuario` (`usuario_id`,`autoridad_perfil_id`),
  CONSTRAINT `cau_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`user_id`),
  CONSTRAINT `cau_ibfk_2` FOREIGN KEY (`autoridad_perfil_id`) REFERENCES `compra_autoridad_perfil` (`autoridad_perfil_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.estimar', 'Revisar el pedido enviado y cargar/confirmar el valor estimado (dpto. de compras)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6)),
    ('compras.presupuesto.autorizar', 'Autorizar el inicio del proceso de pedido de presupuesto', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);

-- Migración de estado (opcional una vez que no queden pedidos leídos por el flujo viejo).
UPDATE compra_pedido SET estado = 'EN_REVISION_COMPRAS' WHERE estado = 'ENVIADO';

-- Los estados nuevos usan hasta 34 caracteres ('PENDIENTE_AUTORIZACION_PRESUPUESTO');
-- las columnas eran varchar(30). Ensanchar a 100 (idempotente).
ALTER TABLE `compra_pedido` MODIFY COLUMN `estado` VARCHAR(100) NOT NULL;
ALTER TABLE `compra_pedido_historial` MODIFY COLUMN `estado` VARCHAR(100) NOT NULL;
