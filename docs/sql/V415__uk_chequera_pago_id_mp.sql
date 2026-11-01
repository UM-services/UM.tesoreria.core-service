-- Idempotencia de los pagos de Mercado Pago: un pago de MP no puede registrarse dos veces.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Motivo: `payment-processed` es un tópico at-least-once (reintentos de Mercado Pago, webhook v1 +
-- IPN legacy, retry del productor, rebalanceo de consumers). El pre-check de
-- `PagoService.registraPagoMP` no alcanza para dos entregas concurrentes: las dos pueden no
-- encontrar el pago y las dos insertar. Este índice es la última defensa: la transacción
-- perdedora falla con `Duplicate entry`, revierte y, al reintentar Kafka, el pre-check encuentra
-- el pago de la ganadora y termina en no-op.
--
-- La columna no declara `@Column` en `ChequeraPagoEntity`, así que Hibernate aplica el naming por
-- defecto: id_mercado_pago. Verificar antes de aplicarlo:
--   SHOW COLUMNS FROM chequera_pago LIKE '%mp%';
--
-- ANTES de crear el índice, detectar duplicados (si devuelve filas, NO borrarlas: son registros
-- contables y se corrigen por reversa/anulación en el circuito administrativo; documentar cada
-- caso en el ticket y recién después crear el índice):
--   SELECT id_mercado_pago, COUNT(*) AS repeticiones, MIN(clave) AS conservar
--   FROM chequera_pago
--   WHERE id_mercado_pago IS NOT NULL AND id_mercado_pago <> ''
--   GROUP BY id_mercado_pago
--   HAVING COUNT(*) > 1;
--
-- MySQL permite múltiples NULL en un índice UNIQUE, así que los pagos que no son de Mercado Pago
-- no se ven afectados.
--
-- Rollback:
--   ALTER TABLE chequera_pago DROP INDEX uk_chequera_pago_id_mp;

SET @existe_indice := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'chequera_pago'
      AND INDEX_NAME = 'uk_chequera_pago_id_mp'
);

SET @ddl := IF(@existe_indice = 0,
    'ALTER TABLE `chequera_pago` ADD UNIQUE KEY `uk_chequera_pago_id_mp` (`id_mercado_pago`)',
    'SELECT ''uk_chequera_pago_id_mp ya existe'' AS mensaje'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
