-- Rollback de la reparación aplicada con el criterio anterior (movía bloques
-- aunque no fueran asientos completos). Restaura desde las tablas bk_* y borra
-- las cabeceras de asiento creadas por la reparación.
--
-- IMPORTANTE: V433 elimina los bk_* al terminar. Este rollback sólo sirve si
-- esos respaldos todavía existen, es decir:
--   * la verificación de V433 no dio 0/0 (el script los conserva), o
--   * se comentó el `CALL limpiar_v433();` antes de ejecutarlo.
-- Pasada la limpieza, el respaldo es el dump previo a la corrida.
--
-- Primero se quita el índice único: los datos originales tienen ítems repetidos
-- (por eso el índice no existía).

SET @existe := (SELECT COUNT(*) FROM information_schema.statistics
                WHERE table_schema = DATABASE() AND table_name = 'movcon'
                  AND index_name = 'uk_movcon_fecha_nrocomp_item');
SET @sql := IF(@existe > 0,
    'ALTER TABLE movcon DROP INDEX uk_movcon_fecha_nrocomp_item',
    'SELECT ''sin indice'' AS mensaje');
PREPARE s FROM @sql;
EXECUTE s;
DEALLOCATE PREPARE s;

START TRANSACTION;

-- 1) Desvincular las FK (valores y entrega apuntan a movcon)
UPDATE valores v
  JOIN bk_valores_mixtos b ON b.Val_ID = v.Val_ID
   SET v.Val_FechaAsiento = NULL, v.Val_NumeroAsiento = NULL;

UPDATE entrega e
  JOIN bk_entrega_mixtos b ON b.NoE_ID = e.NoE_ID
   SET e.NoE_MCo_Fecha = NULL, e.NoE_MCo_NroComp = NULL;

-- 2) Restaurar movcon
UPDATE movcon m
  JOIN bk_movcon_mixtos b ON b.id = m.id
   SET m.MCo_NroComp = b.MCo_NroComp,
       m.MCo_Item = b.MCo_Item,
       m.MCo_TCo_ID = b.MCo_TCo_ID;

-- 3) Restaurar referencias
UPDATE valores v
  JOIN bk_valores_mixtos b ON b.Val_ID = v.Val_ID
   SET v.Val_FechaAsiento = b.Val_FechaAsiento,
       v.Val_NumeroAsiento = b.Val_NumeroAsiento;

UPDATE entrega e
  JOIN bk_entrega_mixtos b ON b.NoE_ID = e.NoE_ID
   SET e.NoE_MCo_Fecha = b.NoE_MCo_Fecha,
       e.NoE_MCo_NroComp = b.NoE_MCo_NroComp;

UPDATE libro_banco l
  JOIN bk_libro_banco_mixtos b ON b.LiB_ID = l.LiB_ID
   SET l.LiB_FechaAsiento = b.LiB_FechaAsiento,
       l.LiB_NumeroAsiento = b.LiB_NumeroAsiento;

UPDATE movprov p
  JOIN bk_movprov_mixtos b ON b.MvP_ID = p.MvP_ID
   SET p.MvP_FechaReg = b.MvP_FechaReg,
       p.MvP_NroComp = b.MvP_NroComp;

UPDATE ingreso_asiento i
  JOIN bk_ingreso_asiento_mixtos b ON b.ina_id = i.ina_id
   SET i.ina_nrocomp = b.ina_nrocomp;

-- 4) Borrar las cabeceras creadas por la reparación (quedaron sin líneas)
DELETE a
FROM asiento a
WHERE DATE(a.created) = CURDATE()
  AND NOT EXISTS (SELECT 1 FROM movcon m WHERE m.MCo_Fecha = a.fecha AND m.MCo_NroComp = a.orden)
  AND NOT EXISTS (SELECT 1 FROM bk_asiento_mixtos b WHERE b.fecha = a.fecha AND b.orden = a.orden);

COMMIT;

SELECT 'mixtos' AS control, COUNT(*) AS valor
FROM (SELECT MCo_Fecha, MCo_NroComp FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) x
UNION ALL
SELECT 'items_repetidos', COUNT(*)
FROM (SELECT 1 FROM movcon GROUP BY MCo_Fecha, MCo_NroComp, MCo_Item HAVING COUNT(*) > 1) y
UNION ALL
SELECT 'descuadrados', COUNT(*)
FROM (SELECT MCo_Fecha, MCo_NroComp FROM movcon GROUP BY 1, 2
      HAVING SUM(CASE WHEN MCo_Debita = 1 THEN MCo_Importe ELSE -MCo_Importe END) <> 0) z;
