-- `chequera_borrador_cuota`: se quitan los prefijos `chbc_` de todas las columnas y la PK pasa a
-- llamarse `chequera_borrador_cuota_id`.
--
-- Base: tesium (MySQL 5.7). Reproducible con ddl-auto: none. Idempotente (guard por
-- information_schema: si `chbc_id` ya no existe, el script no hace nada).
--
-- Para qué: `V423__chequera_borrador_emision.sql` ya quedó con los nombres definitivos, así que
-- este script sólo aplica a las bases donde la tabla se había creado con el prefijo `chbc_`.
--
-- Ordinales: `chbc_1er_vencimiento` / `chbc_1er_importe_original` / `chbc_1er_importe_borrador`
-- pasan a `vencimiento_1` / `importe_original_1` / `importe_borrador_1` (y 2 y 3), para no dejar
-- columnas que empiecen con dígito.
--
-- Se usa `CHANGE COLUMN` (con el tipo completo) y no `RENAME COLUMN`, que recién existe desde
-- MySQL 8.0. Los tipos son los mismos con los que se creó la tabla en `V423`.
--
-- Orden: aplicar `V426` (padre) antes de este script. La FK
-- `fk_chequera_borrador_cuota_borrador` se mantiene: sólo cambia el nombre de su columna y sigue
-- apuntando a `chequera_borrador.chequera_borrador_id`.
--
-- Verificar antes y después:
--   SHOW COLUMNS FROM chequera_borrador_cuota LIKE 'chbc%';
--   SHOW CREATE TABLE chequera_borrador_cuota\G

SET @existe := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME   = 'chequera_borrador_cuota'
    AND COLUMN_NAME  = 'chbc_id'
);

SET @ddl := IF(@existe = 1,
  'ALTER TABLE `chequera_borrador_cuota`
     CHANGE COLUMN `chbc_id` `chequera_borrador_cuota_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
     CHANGE COLUMN `chbc_borrador_id` `borrador_id` BIGINT(20) NOT NULL,
     CHANGE COLUMN `chbc_chequera_cuota_id` `chequera_cuota_id` BIGINT(20) NULL,
     CHANGE COLUMN `chbc_producto_id` `producto_id` INT(11) NOT NULL,
     CHANGE COLUMN `chbc_alternativa_id` `alternativa_id` INT(11) NOT NULL,
     CHANGE COLUMN `chbc_cuota_id` `cuota_id` INT(11) NOT NULL,
     CHANGE COLUMN `chbc_mes` `mes` INT(11) NULL,
     CHANGE COLUMN `chbc_anio` `anio` INT(11) NULL,
     CHANGE COLUMN `chbc_1er_vencimiento` `vencimiento_1` DATETIME(6) NULL,
     CHANGE COLUMN `chbc_2do_vencimiento` `vencimiento_2` DATETIME(6) NULL,
     CHANGE COLUMN `chbc_3er_vencimiento` `vencimiento_3` DATETIME(6) NULL,
     CHANGE COLUMN `chbc_1er_importe_original` `importe_original_1` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
     CHANGE COLUMN `chbc_2do_importe_original` `importe_original_2` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
     CHANGE COLUMN `chbc_3er_importe_original` `importe_original_3` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
     CHANGE COLUMN `chbc_1er_importe_borrador` `importe_borrador_1` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
     CHANGE COLUMN `chbc_2do_importe_borrador` `importe_borrador_2` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
     CHANGE COLUMN `chbc_3er_importe_borrador` `importe_borrador_3` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
     CHANGE COLUMN `chbc_arancel_tipo_id` `arancel_tipo_id` INT(11) NULL,
     CHANGE COLUMN `chbc_tramo_id` `tramo_id` INT(11) NULL,
     CHANGE COLUMN `chbc_descuento_pt` `descuento_pt` DECIMAL(9,4) NOT NULL DEFAULT 0.0000,
     CHANGE COLUMN `chbc_pagado` `pagado` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chbc_baja` `baja` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chbc_manual` `manual` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chbc_compensada` `compensada` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chbc_modificado` `modificado` TINYINT(4) NOT NULL DEFAULT 0,
     CHANGE COLUMN `chbc_bloqueado` `bloqueado` TINYINT(4) NOT NULL DEFAULT 0',
  'DO 0'
);

PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
