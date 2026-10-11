-- Etapa 7 — Adjudicación por monto (decisión de la autoridad sobre la cotización recomendada).
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Modelo (decisiones §15 de PLANIFICACION_COMPRAS.md):
--   * compra_adjudicacion = acto de adjudicación: registra la decisión de la autoridad competente
--     (ADJUDICADA / DEVUELTA_A_COMPRAS / NO_ADJUDICADA) sobre la cotización recomendada, con el
--     monto evaluado, la autoridad, el límite aplicado (snapshot; NULL = ilimitado) y el fundamento.
--
-- El esquema de valores REUTILIZA el de la autorización de presupuesto (§11):
--   compra_referencia + compra_autoridad_perfil + compra_autoridad_usuario.
-- El límite se valida en la fachada (fail-closed); acá sólo se persiste el acto.
-- La decisión aplica al proceso de cotización un estado nuevo y, si ADJUDICADA, avanza a
-- ADJUDICADO los pedidos cubiertos (lo hace la aplicación).

CREATE TABLE IF NOT EXISTS `compra_adjudicacion` (
  `compra_adjudicacion_id`         bigint        NOT NULL AUTO_INCREMENT,
  `compra_cotizacion_id`           bigint        NOT NULL,
  `ejercicio_id`                   smallint(2)   DEFAULT NULL,
  `compra_cotizacion_proveedor_id` bigint        DEFAULT NULL,
  `proveedor_adjudicado_id`        smallint(2)   DEFAULT NULL,
  `monto`                          decimal(19,2) DEFAULT NULL,
  `decision`                       varchar(20)   NOT NULL,
  `fundamento`                     varchar(1000) DEFAULT NULL,
  `autoridad_usuario_id`           int(4)        NOT NULL,
  `limite_aplicado`                decimal(19,2) DEFAULT NULL,
  `fecha`                          datetime(6)   DEFAULT NULL,
  `created`                        datetime      DEFAULT NULL,
  `updated`                        timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`compra_adjudicacion_id`),
  KEY `ix_compra_adjudicacion_cotizacion` (`compra_cotizacion_id`),
  KEY `ix_compra_adjudicacion_autoridad` (`autoridad_usuario_id`),
  CONSTRAINT `compra_adjudicacion_ibfk_1` FOREIGN KEY (`compra_cotizacion_id`) REFERENCES `compra_cotizacion` (`compra_cotizacion_id`),
  CONSTRAINT `compra_adjudicacion_ibfk_2` FOREIGN KEY (`autoridad_usuario_id`) REFERENCES `usuario` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Estados nuevos del proceso de cotización (ADJUDICADA, DEVUELTA_A_COMPRAS, NO_ADJUDICADA):
-- se ensancha la columna (mismo criterio que D-AUT-10 en V407).
ALTER TABLE `compra_cotizacion` MODIFY COLUMN `estado` VARCHAR(100) NOT NULL;

-- Permiso de la etapa 7. La asignación a roles/usuarios (rol_permiso / usuario_permiso) es
-- administración de datos y no se versiona acá.
INSERT INTO permiso (clave, descripcion, modulo, aplicacion, activo, created, updated)
VALUES
    ('compras.adjudicacion.autorizar', 'Adjudicar la cotización recomendada o devolverla a compras (autoridad por monto)', 'compras', 'TESORERIA', 1, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    modulo = VALUES(modulo),
    activo = 1,
    updated = NOW(6);
