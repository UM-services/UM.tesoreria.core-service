-- Orden de los eventos de pago de Mercado Pago por contexto.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Motivo: Kafka garantiza orden por key, y la key del tópico `payment-processed` es el
-- `paymentId`, no el contexto. Dos pagos distintos del mismo contexto (reintento de compra,
-- refund + nueva aprobación) pueden llegar desordenados y un evento viejo pisaría el estado del
-- más nuevo. Con `date_last_updated` del pago, core descarta los eventos anteriores al último
-- aplicado (`ProcessPaymentEventUseCaseImpl.eventoDesactualizado`).
--
-- La fecha se guarda como DATETIME(6) (convención del workspace para columnas nuevas, aunque las
-- columnas vecinas de esta tabla sean DATETIME sin fracción).
--
-- Rollback:
--   ALTER TABLE mercado_pago_context DROP COLUMN date_last_updated;

SET @existe_columna := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'mercado_pago_context'
      AND COLUMN_NAME = 'date_last_updated'
);

SET @ddl := IF(@existe_columna = 0,
    'ALTER TABLE `mercado_pago_context` ADD COLUMN `date_last_updated` DATETIME(6) NULL',
    'SELECT ''mercado_pago_context.date_last_updated ya existe'' AS mensaje'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
