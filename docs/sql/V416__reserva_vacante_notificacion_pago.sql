-- Idempotencia de la notificación de pago de reservas de vacante.
-- Base: tesium. Reproducible con ddl-auto: none. Idempotente.
--
-- Motivo: `payment-processed` es at-least-once y `umhub-service` no tiene base de datos. Sin una
-- marca de "ya notificado", cada reintento (o el webhook v1 + el IPN legacy del mismo pago) vuelve
-- a disparar el POST al webhook de n8n. La marca vive acá, en el servicio dueño de
-- `reserva_vacante`, y se reclama con un claim atómico (`SELECT ... FOR UPDATE`).
--
-- Ciclo de vida:
--   0 = pendiente de notificar (o liberada porque el envío falló)
--   1 = reclamada por una instancia; el reintento de Kafka no vuelve a notificar
--
-- Rollback:
--   ALTER TABLE reserva_vacante DROP COLUMN notificacion_pago;

SET @existe_columna := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'reserva_vacante'
      AND COLUMN_NAME = 'notificacion_pago'
);

SET @ddl := IF(@existe_columna = 0,
    'ALTER TABLE `reserva_vacante` ADD COLUMN `notificacion_pago` TINYINT(1) NOT NULL DEFAULT 0',
    'SELECT ''reserva_vacante.notificacion_pago ya existe'' AS mensaje'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
