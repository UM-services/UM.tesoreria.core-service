-- Cambio de clave forzado: el reset de administración deja una clave provisoria y marca
-- `debe_cambiar_clave = 1`; el usuario debe cambiarla al próximo ingreso (el login la expone
-- y `change-password` la limpia).
--
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente (guard por information_schema,
-- porque MySQL 8 no soporta ADD COLUMN IF NOT EXISTS).
--
-- La administración de datos (a quién se le reseteó la clave) no se versiona.

SET @existe := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME   = 'usuario'
    AND COLUMN_NAME  = 'debe_cambiar_clave'
);
SET @ddl := IF(@existe = 0,
  'ALTER TABLE `usuario` ADD COLUMN `debe_cambiar_clave` tinyint(1) NOT NULL DEFAULT 0',
  'DO 0');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
