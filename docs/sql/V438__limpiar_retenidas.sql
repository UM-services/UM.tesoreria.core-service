-- V438 — Envío de chequeras: retiro de la retención por deuda
--
-- La política que marcaba `chequera_serie.retenida = 1` y enviaba una notificación de deuda en
-- lugar de la chequera (frmCheqMail, > 3 cuotas impagas del lectivo) se retiró: el envío masivo
-- manda siempre la chequera. La columna se conserva (la leen report-service y el padrón), pero
-- deja de escribirse desde core.
--
-- Ejecutar UNA vez después de desplegar el core con la política retirada.

UPDATE chequera_serie
   SET retenida = 0
 WHERE retenida IS NOT NULL
   AND retenida <> 0;
