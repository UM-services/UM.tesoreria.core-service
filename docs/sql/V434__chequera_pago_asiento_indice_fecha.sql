-- V434__chequera_pago_asiento_indice_fecha.sql
--
-- Índice de lectura por día/tipo de pago de `chequera_pago_asiento` (3,5 M de filas).
--
-- La regeneración del asiento de ingreso y `/pago/items` filtran siempre por
-- (tipo_pago_id, fecha); con sólo el índice de `tipo_pago_id` la consulta/borrado recorre todas
-- las filas de ese tipo de pago (más de 200 k) para quedarse con un día.
--
-- No se crea por defecto al arrancar el servicio (ddl-auto = none): aplicarlo a mano.
-- MySQL no soporta CREATE INDEX IF NOT EXISTS, así que el script es idempotente por chequeo.

SET @existe := (SELECT COUNT(*) FROM information_schema.statistics
                 WHERE table_schema = DATABASE()
                   AND table_name = 'chequera_pago_asiento'
                   AND index_name = 'idx_cpa_tipo_pago_fecha');

SET @sql := IF(@existe = 0,
    'CREATE INDEX idx_cpa_tipo_pago_fecha ON chequera_pago_asiento (tipo_pago_id, fecha)',
    'SELECT ''indice ya existente'' AS mensaje');
PREPARE s FROM @sql;
EXECUTE s;
DEALLOCATE PREPARE s;
