-- Bandeja de solicitudes de acceso (pedidos de permiso enviados desde el frontend).
-- Base: tesium. La aplicación NO crea ni migra el esquema (ddl-auto: none).
-- No modifica tablas existentes ni contratos legacy.
CREATE TABLE IF NOT EXISTS `solicitud_acceso` (
    `solicitud_acceso_id` bigint       NOT NULL AUTO_INCREMENT,
    `user_id`             int          DEFAULT NULL,
    `aplicacion`          varchar(20)  NOT NULL DEFAULT 'TESORERIA',
    `clave`               varchar(120) DEFAULT NULL,
    `modulo`              varchar(60)  DEFAULT NULL,
    `comentario`          varchar(500) DEFAULT NULL,
    `estado`              varchar(20)  NOT NULL DEFAULT 'PENDIENTE',
    `observacion`         varchar(500) DEFAULT NULL,
    `resuelto_por`        int          DEFAULT NULL,
    `resuelto`            datetime(6)  DEFAULT NULL,
    `created`             datetime(6)  NOT NULL,
    `updated`             datetime(6)  DEFAULT NULL,
    PRIMARY KEY (`solicitud_acceso_id`),
    KEY `ix_solicitud_acceso_pendiente` (`estado`, `created`),
    KEY `ix_solicitud_acceso_usuario` (`user_id`, `created`),
    CONSTRAINT `solicitud_acceso_ibfk_1`
        FOREIGN KEY (`user_id`) REFERENCES `usuario` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
