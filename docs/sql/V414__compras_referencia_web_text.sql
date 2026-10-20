-- Referencia web del ítem de pedido: de varchar(500) a TEXT.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Motivo: las URLs de referencia de productos (marketplaces, buscadores) llevan parámetros de
-- seguimiento y superan con facilidad los 500 caracteres. El valor es de sólo referencia para el
-- solicitante y el área de compras, por lo que no necesita un tope corto.
--
-- La entidad `CompraPedidoItemEntity` declara la columna con `length = 65535`, que Hibernate
-- traduce a TEXT en MySQL. Si la columna todavía no existe (bases creadas antes del slice de
-- ítems), se agrega con el mismo tipo.

SET @existe_columna := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'compra_pedido_item'
    AND COLUMN_NAME = 'referencia_web'
);
SET @ddl := IF(@existe_columna = 0,
  'ALTER TABLE `compra_pedido_item` ADD COLUMN `referencia_web` TEXT NULL AFTER `especificaciones`',
  'ALTER TABLE `compra_pedido_item` MODIFY COLUMN `referencia_web` TEXT NULL');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
