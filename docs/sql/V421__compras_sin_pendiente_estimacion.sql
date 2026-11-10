-- Elimina el estado PENDIENTE_ESTIMACION del circuito del pedido de compra.
-- Los pedidos que estaban en ese estado pasan a PENDIENTE_ENVIO: el autorizante de envío
-- puede aprobarlos sin monto y el dpto. de compras carga la estimación en la revisión.
--
-- Base: tesium. Sin cambios de esquema. Idempotente.

INSERT INTO compra_pedido_historial (compra_pedido_id, estado, usuario_id, observacion, fecha)
SELECT p.compra_pedido_id, 'PENDIENTE_ENVIO', NULL,
       'Migración: se elimina el estado PENDIENTE_ESTIMACION', NOW(6)
FROM compra_pedido p
WHERE p.estado = 'PENDIENTE_ESTIMACION'
  AND NOT EXISTS (
      SELECT 1
      FROM compra_pedido_historial h
      WHERE h.compra_pedido_id = p.compra_pedido_id
        AND h.estado = 'PENDIENTE_ENVIO'
        AND h.observacion = 'Migración: se elimina el estado PENDIENTE_ESTIMACION'
  );

UPDATE compra_pedido
SET estado = 'PENDIENTE_ENVIO'
WHERE estado = 'PENDIENTE_ESTIMACION';
