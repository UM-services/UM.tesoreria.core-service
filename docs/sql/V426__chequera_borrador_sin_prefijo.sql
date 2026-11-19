-- `chequera_borrador`: se quitan los prefijos `chb_` de todas las columnas y la PK pasa a
-- llamarse `chequera_borrador_id`.
--
-- Base: tesium (MySQL 5.7). Reproducible con ddl-auto: none. Idempotente (guard por
-- information_schema: si `chb_id` ya no existe, el script no hace nada).
--
-- Para qué: `V423__chequera_borrador_emision.sql` ya quedó con los nombres definitivos, así que
-- este script sólo aplica a las bases donde la tabla se había creado con el prefijo `chb_`.
--
-- Se usa `CHANGE COLUMN` (con el tipo completo) y no `RENAME COLUMN`, que recién existe desde
-- MySQL 8.0. Los tipos son los mismos con los que se creó la tabla en `V423`.
--
-- Orden: aplicar `V426` (padre) antes de `V427` (hija). MySQL actualiza sola la FK
-- `fk_chequera_borrador_cuota_borrador` al renombrar la columna referenciada.
--
-- Verificar antes y después:
--   SHOW COLUMNS FROM chequera_borrador LIKE 'chb%';
--   SHOW CREATE TABLE chequera_borrador_cuota\G

SET @existe := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME   = 'chequera_borrador'
    AND COLUMN_NAME  = 'chb_id'
);

SET @ddl := IF(@existe = 1,
  'ALTER TABLE `chequera_borrador`
     CHANGE COLUMN `chb_id` `chequera_borrador_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
     CHANGE COLUMN `chb_estado` `estado` VARCHAR(16) NOT NULL DEFAULT ''ABIERTO'',
     CHANGE COLUMN `chb_persona_id` `persona_id` DECIMAL(19,0) NOT NULL,
     CHANGE COLUMN `chb_documento_id` `documento_id` INT(11) NOT NULL,
     CHANGE COLUMN `chb_facultad_id` `facultad_id` INT(11) NOT NULL,
     CHANGE COLUMN `chb_lectivo_id` `lectivo_id` INT(11) NOT NULL,
     CHANGE COLUMN `chb_tipo_chequera_id` `tipo_chequera_id` INT(11) NOT NULL,
     CHANGE COLUMN `chb_arancel_tipo_id` `arancel_tipo_id` INT(11) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chb_alternativa_id` `alternativa_id` INT(11) NULL,
     CHANGE COLUMN `chb_curso_id` `curso_id` INT(11) NULL,
     CHANGE COLUMN `chb_chequera_serie_id` `chequera_serie_id` BIGINT(20) NULL,
     CHANGE COLUMN `chb_fecha` `fecha` DATETIME(6) NULL,
     CHANGE COLUMN `chb_hpum` `hpum` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chb_beca_porcentaje` `beca_porcentaje` DECIMAL(9,4) NOT NULL DEFAULT 0.0000,
     CHANGE COLUMN `chb_beca_resolucion` `beca_resolucion` VARCHAR(255) NULL,
     CHANGE COLUMN `chb_beca_fecha` `beca_fecha` DATETIME(6) NULL,
     CHANGE COLUMN `chb_beca_user_id` `beca_user_id` BIGINT(20) NULL,
     CHANGE COLUMN `chb_calcula_beca` `calcula_beca` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chb_email_personal` `email_personal` VARCHAR(255) NULL,
     CHANGE COLUMN `chb_email_institucional` `email_institucional` VARCHAR(255) NULL,
     CHANGE COLUMN `chb_email_copia` `email_copia` VARCHAR(255) NULL,
     CHANGE COLUMN `chb_incluye_barras` `incluye_barras` TINYINT(4) NOT NULL DEFAULT 1,
     CHANGE COLUMN `chb_copia_facultad` `copia_facultad` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chb_chequera_id` `chequera_id` BIGINT(20) NULL,
     CHANGE COLUMN `chb_chequera_serie_id_emitida` `chequera_serie_id_emitida` BIGINT(20) NULL,
     CHANGE COLUMN `chb_auditoria_id` `auditoria_id` VARCHAR(36) NULL,
     CHANGE COLUMN `chb_clave_idempotencia` `clave_idempotencia` VARCHAR(64) NULL,
     CHANGE COLUMN `chb_usuario_id` `usuario_id` VARCHAR(64) NULL,
     CHANGE COLUMN `chb_version` `version` INT(11) NOT NULL DEFAULT 0',
  'DO 0'
);

PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
