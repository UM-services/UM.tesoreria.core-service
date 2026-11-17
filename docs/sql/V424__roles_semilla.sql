-- V424 — Semilla de roles RBAC (recuperación tras un restore de `tesium`).
--
-- Espeja src/main/resources/roles.yaml (misma matriz). El servicio también la
-- aplica al arrancar; este script es el camino offline para cuando la base se
-- restaura desde un dump sin las tablas/datos de permisos.
--
-- Idempotente: se puede ejecutar N veces sin duplicar roles ni asignaciones.
-- Requiere que el catálogo `permiso` esté poblado (lo sincroniza tesoreria-core
-- al arrancar desde `permisos.yaml`). Este script NO crea claves de permiso.
--
-- Rollback (borra primero las asignaciones por las FKs):
--   DELETE FROM rol_permiso WHERE rol_id IN (
--     SELECT rol_id FROM rol WHERE aplicacion = 'TESORERIA' AND nombre IN
--       ('ADMIN_TESORERIA','OPERADOR_CHEQUERAS','OPERADOR_COMPRAS_PAGOS','CONSULTA_EXTERNO'));
--   DELETE FROM usuario_rol WHERE rol_id IN (...misma subconsulta...);
--   DELETE FROM rol WHERE aplicacion = 'TESORERIA' AND nombre IN (...);

-- ---------------------------------------------------------------------------
-- Esquema (sólo si falta; ver PLANIFICACION_PERMISOS.md §3)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `rol` (
    `rol_id`      bigint       NOT NULL AUTO_INCREMENT,
    `nombre`      varchar(80)  NOT NULL,
    `descripcion` varchar(255) DEFAULT NULL,
    `aplicacion`  varchar(20)  NOT NULL DEFAULT 'TESORERIA',
    `activo`      tinyint      NOT NULL DEFAULT 1,
    `created`     datetime(6)  NOT NULL,
    `updated`     datetime(6)  NOT NULL,
    PRIMARY KEY (`rol_id`),
    UNIQUE KEY `uq_rol_nombre` (`aplicacion`, `nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `permiso` (
    `permiso_id`  bigint       NOT NULL AUTO_INCREMENT,
    `clave`       varchar(120) NOT NULL,
    `descripcion` varchar(255) NOT NULL,
    `modulo`      varchar(60)  NOT NULL,
    `aplicacion`  varchar(20)  NOT NULL DEFAULT 'TESORERIA',
    `activo`      tinyint      NOT NULL DEFAULT 1,
    `created`     datetime(6)  NOT NULL,
    `updated`     datetime(6)  NOT NULL,
    PRIMARY KEY (`permiso_id`),
    UNIQUE KEY `uq_permiso_clave` (`aplicacion`, `clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `rol_permiso` (
    `rol_permiso_id` bigint      NOT NULL AUTO_INCREMENT,
    `rol_id`         bigint      NOT NULL,
    `permiso_id`     bigint      NOT NULL,
    `created`        datetime(6) NOT NULL,
    `updated`        datetime(6) NOT NULL,
    PRIMARY KEY (`rol_permiso_id`),
    UNIQUE KEY `uq_rol_permiso` (`rol_id`, `permiso_id`),
    CONSTRAINT `fk_rp_rol`     FOREIGN KEY (`rol_id`)     REFERENCES `rol` (`rol_id`),
    CONSTRAINT `fk_rp_permiso` FOREIGN KEY (`permiso_id`) REFERENCES `permiso` (`permiso_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `usuario_rol` (
    `usuario_rol_id` bigint      NOT NULL AUTO_INCREMENT,
    `user_id`        int         NOT NULL,
    `rol_id`         bigint      NOT NULL,
    `created`        datetime(6) NOT NULL,
    `updated`        datetime(6) NOT NULL,
    PRIMARY KEY (`usuario_rol_id`),
    UNIQUE KEY `uq_usuario_rol` (`user_id`, `rol_id`),
    CONSTRAINT `fk_ur_usuario` FOREIGN KEY (`user_id`) REFERENCES `usuario` (`user_id`),
    CONSTRAINT `fk_ur_rol`     FOREIGN KEY (`rol_id`)  REFERENCES `rol` (`rol_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Roles semilla
-- ---------------------------------------------------------------------------
INSERT INTO `rol` (`nombre`, `descripcion`, `aplicacion`, `activo`, `created`, `updated`)
SELECT 'ADMIN_TESORERIA', 'Administración completa de tesorería: todos los permisos de TESORERIA, incluidos roles y permisos', 'TESORERIA', 1, NOW(6), NOW(6)
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `rol` WHERE `aplicacion` = 'TESORERIA' AND `nombre` = 'ADMIN_TESORERIA')
;

INSERT INTO `rol` (`nombre`, `descripcion`, `aplicacion`, `activo`, `created`, `updated`)
SELECT 'OPERADOR_CHEQUERAS', 'Operación completa de chequeras: emisión, impresión, numeración, cobranzas y consultas', 'TESORERIA', 1, NOW(6), NOW(6)
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `rol` WHERE `aplicacion` = 'TESORERIA' AND `nombre` = 'OPERADOR_CHEQUERAS')
;

INSERT INTO `rol` (`nombre`, `descripcion`, `aplicacion`, `activo`, `created`, `updated`)
SELECT 'OPERADOR_COMPRAS_PAGOS', 'Operación del circuito de compras y de comprobantes de pago', 'TESORERIA', 1, NOW(6), NOW(6)
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `rol` WHERE `aplicacion` = 'TESORERIA' AND `nombre` = 'OPERADOR_COMPRAS_PAGOS')
;

INSERT INTO `rol` (`nombre`, `descripcion`, `aplicacion`, `activo`, `created`, `updated`)
SELECT 'CONSULTA_EXTERNO', 'Consulta externa (usuarioExterno): sin claves propias en el catálogo actual', 'TESORERIA', 1, NOW(6), NOW(6)
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `rol` WHERE `aplicacion` = 'TESORERIA' AND `nombre` = 'CONSULTA_EXTERNO')
;

-- ---------------------------------------------------------------------------
-- Matriz rol × permiso (equivalente a los comodines de roles.yaml)
-- ---------------------------------------------------------------------------

-- ADMIN_TESORERIA: "*" → todo el catálogo de TESORERIA.
INSERT INTO `rol_permiso` (`rol_id`, `permiso_id`, `created`, `updated`)
SELECT r.`rol_id`, p.`permiso_id`, NOW(6), NOW(6)
FROM `rol` r
JOIN `permiso` p
  ON p.`aplicacion` = 'TESORERIA'
 AND p.`activo` = 1
WHERE r.`aplicacion` = 'TESORERIA'
  AND r.`nombre` = 'ADMIN_TESORERIA'
  AND NOT EXISTS (
      SELECT 1 FROM `rol_permiso` rp
      WHERE rp.`rol_id` = r.`rol_id` AND rp.`permiso_id` = p.`permiso_id`)
;

-- OPERADOR_CHEQUERAS: "chequeras.*".
INSERT INTO `rol_permiso` (`rol_id`, `permiso_id`, `created`, `updated`)
SELECT r.`rol_id`, p.`permiso_id`, NOW(6), NOW(6)
FROM `rol` r
JOIN `permiso` p
  ON p.`aplicacion` = 'TESORERIA'
 AND p.`activo` = 1
 AND p.`clave` LIKE 'chequeras.%'
WHERE r.`aplicacion` = 'TESORERIA'
  AND r.`nombre` = 'OPERADOR_CHEQUERAS'
  AND NOT EXISTS (
      SELECT 1 FROM `rol_permiso` rp
      WHERE rp.`rol_id` = r.`rol_id` AND rp.`permiso_id` = p.`permiso_id`)
;

-- OPERADOR_COMPRAS_PAGOS: "compras.*" + "pagos.*".
INSERT INTO `rol_permiso` (`rol_id`, `permiso_id`, `created`, `updated`)
SELECT r.`rol_id`, p.`permiso_id`, NOW(6), NOW(6)
FROM `rol` r
JOIN `permiso` p
  ON p.`aplicacion` = 'TESORERIA'
 AND p.`activo` = 1
 AND (p.`clave` LIKE 'compras.%' OR p.`clave` LIKE 'pagos.%')
WHERE r.`aplicacion` = 'TESORERIA'
  AND r.`nombre` = 'OPERADOR_COMPRAS_PAGOS'
  AND NOT EXISTS (
      SELECT 1 FROM `rol_permiso` rp
      WHERE rp.`rol_id` = r.`rol_id` AND rp.`permiso_id` = p.`permiso_id`)
;

-- CONSULTA_EXTERNO: sin claves (el acceso externo lo controla usuario.usuarioExterno).

-- ---------------------------------------------------------------------------
-- Asignación de un rol a un usuario (ejemplo; reemplazar <userId>)
-- ---------------------------------------------------------------------------
-- INSERT INTO `usuario_rol` (`user_id`, `rol_id`, `created`, `updated`)
-- SELECT 123, r.`rol_id`, NOW(6), NOW(6)
-- FROM `rol` r
-- WHERE r.`aplicacion` = 'TESORERIA'
--   AND r.`nombre` = 'OPERADOR_CHEQUERAS'
--   AND NOT EXISTS (
--       SELECT 1 FROM `usuario_rol` ur
--       WHERE ur.`user_id` = 123 AND ur.`rol_id` = r.`rol_id`);
