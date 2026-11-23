-- ============================================================================
-- V433__movcon_asientos_mixtos.sql
--
-- Separa los comprobantes que quedaron mezclados en un mismo asiento de
-- `movcon` y deja el índice único (MCo_Fecha, MCo_NroComp, MCo_Item) creado.
--
-- Problema
-- --------
-- `nextAsiento` del legacy no era transaccional: el asiento de una orden de
-- pago, de un valor, de una factura o de una entrega podía escribirse dentro
-- del asiento que ya pertenecía al ingreso del día. Resultado: un mismo
-- (fecha, orden) con líneas de dos comprobantes y, además, ítems repetidos.
--
-- Criterio de reparación (protege el cuadre de cada asiento)
-- ---------------------------------------------------------
-- Para cada asiento mixto se define primero el comprobante dueño:
--   1. el ingreso (MCo_TCo_ID = 3) si el asiento tiene líneas de ingreso;
--   2. si no, el comprobante con referencia externa (`movprov`, `valores`,
--      `entrega`) que tenga líneas propias;
--   3. si no hay referencias, el bloque con más líneas.
--
-- Después, cada bloque de otro comprobante:
--   a. Si es un asiento completo (2 o más líneas y saldo cero) se MUEVE a un
--      asiento nuevo de la misma fecha, con sus referencias actualizadas.
--      Es el caso de los valores de una OP o de una factura que se escribieron
--      dentro del asiento del ingreso.
--   b. Si no es un asiento completo (por ejemplo, la única línea de ajuste del
--      propio ingreso que quedó marcada con MCo_TCo_ID = 1) se REETIQUETA con
--      el comprobante dueño: no cambia ningún importe, el asiento sigue
--      cuadrando y deja de estar mezclado.
--
-- Ninguna de las dos operaciones altera el saldo de los asientos: mover un
-- bloque equilibrado deja equilibrado al resto, y reetiquetar no toca importes.
--
-- Respaldo: antes de tocar nada se copian (CREATE TABLE IF NOT EXISTS) las
-- filas afectadas de movcon, asiento, valores, movprov, entrega,
-- ingreso_asiento y libro_banco en tablas bk_*.
--
-- Esos bk_* y los procedimientos de trabajo son temporales: al final (sección 6)
-- el script los elimina para no dejar objetos propios en la base. Sólo se
-- conservan si la verificación no dio 0 asientos mixtos y 0 ítems repetidos.
--
-- Idempotente: si no quedan asientos mixtos no hace nada y el índice sólo se
-- crea si todavía no existe.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 0. Respaldo
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bk_asientos_mixtos AS
SELECT g.f AS fecha, g.o AS orden, COUNT(*) AS lineas, COUNT(DISTINCT m.MCo_TCo_ID) AS comprobantes
FROM (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
JOIN movcon m ON m.MCo_Fecha = g.f AND m.MCo_NroComp = g.o
GROUP BY g.f, g.o;

CREATE TABLE IF NOT EXISTS bk_movcon_mixtos AS
SELECT m.*
FROM movcon m
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = m.MCo_Fecha AND g.o = m.MCo_NroComp;

CREATE TABLE IF NOT EXISTS bk_asiento_mixtos AS
SELECT a.*
FROM asiento a
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = a.fecha AND g.o = a.orden;

CREATE TABLE IF NOT EXISTS bk_valores_mixtos AS
SELECT v.*
FROM valores v
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = v.Val_FechaAsiento AND g.o = v.Val_NumeroAsiento;

CREATE TABLE IF NOT EXISTS bk_movprov_mixtos AS
SELECT p.*
FROM movprov p
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = p.MvP_FechaReg AND g.o = p.MvP_NroComp;

CREATE TABLE IF NOT EXISTS bk_entrega_mixtos AS
SELECT e.*
FROM entrega e
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = e.NoE_MCo_Fecha AND g.o = e.NoE_MCo_NroComp;

CREATE TABLE IF NOT EXISTS bk_ingreso_asiento_mixtos AS
SELECT i.*
FROM ingreso_asiento i
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = i.ina_fecha AND g.o = i.ina_nrocomp;

CREATE TABLE IF NOT EXISTS bk_libro_banco_mixtos AS
SELECT b.*
FROM libro_banco b
JOIN (SELECT MCo_Fecha f, MCo_NroComp o FROM movcon GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) g
  ON g.f = b.LiB_FechaAsiento AND g.o = b.LiB_NumeroAsiento;

-- ----------------------------------------------------------------------------
-- 1. Reparación
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS fix_asientos_mixtos;

DELIMITER $$
CREATE PROCEDURE fix_asientos_mixtos()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE vfecha DATETIME;
    DECLARE vorden SMALLINT;
    DECLARE vtco SMALLINT;
    DECLARE vowner SMALLINT;
    DECLARE vlineas INT;
    DECLARE vsaldo DECIMAL(16, 2);
    DECLARE vnuevo SMALLINT;
    DECLARE vdest_val SMALLINT;
    DECLARE vdest_ent SMALLINT;
    DECLARE vvinculo VARCHAR(300) DEFAULT NULL;

    DECLARE cur CURSOR FOR
        SELECT t.fecha, t.orden, t.tco, t.owner, t.lineas, t.saldo
        FROM tmp_trabajo t
        WHERE t.tco <> t.owner
        ORDER BY t.fecha, t.orden, t.tco;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    START TRANSACTION;

    repara: LOOP
        FETCH cur INTO vfecha, vorden, vtco, vowner, vlineas, vsaldo;
        IF done = 1 THEN
            LEAVE repara;
        END IF;

        IF vsaldo = 0 AND vlineas >= 2 THEN
            -- Bloque que es un asiento completo: se mueve a un asiento propio.

            SET vnuevo = GREATEST(
                    IFNULL((SELECT MAX(MCo_NroComp) FROM movcon WHERE MCo_Fecha = vfecha), 0),
                    IFNULL((SELECT MAX(orden) FROM asiento WHERE fecha = vfecha), 0)) + 1;

            SET vvinculo = NULL;
            IF vtco = 3 THEN
                SET vvinculo = 'Asiento Ingreso';
            ELSEIF vtco = 6 THEN
                SET vvinculo = (SELECT CONCAT('Pago ', LPAD(p.MvP_Prefijo, 4, '0'), '-', LPAD(p.MvP_NroComprob, 8, '0'))
                                  FROM valores v
                                  JOIN movprov_detallevalores d ON d.OpV_Val_ID = v.Val_ID
                                  JOIN movprov p ON p.MvP_ID = d.OpV_OrP_ID
                                 WHERE v.Val_FechaAsiento = vfecha AND v.Val_NumeroAsiento = vorden
                                 LIMIT 1);
            ELSEIF vtco IN (4, 5, 7, 8, 9) THEN
                SET vvinculo = (SELECT CONCAT('Factura de Compra ', LPAD(p.MvP_Prefijo, 4, '0'), '-', LPAD(p.MvP_NroComprob, 8, '0'))
                                  FROM movprov p
                                 WHERE p.MvP_FechaReg = vfecha AND p.MvP_NroComp = vorden AND p.MvP_TCo_ID = vtco
                                 LIMIT 1);
            END IF;

            IF vvinculo IS NULL OR vvinculo = '' THEN
                SET vvinculo = (SELECT a.vinculo FROM asiento a
                                 WHERE a.fecha = vfecha AND a.orden = vorden LIMIT 1);
            END IF;

            IF vvinculo IS NULL OR vvinculo = '' THEN
                SET vvinculo = CASE
                    WHEN vtco = 3 THEN 'Asiento Ingreso'
                    WHEN vtco = 6 THEN 'Valor OP Proveedores'
                    WHEN vtco IN (4, 5, 7, 8, 9) THEN 'Factura de Compra'
                    ELSE 'Registro de Asiento'
                END;
            END IF;

            -- `valores` y `entrega` tienen FK contra movcon(fecha, orden): se
            -- desvinculan antes de mover y se re-vinculan al asiento correcto.
            DROP TEMPORARY TABLE IF EXISTS tmp_valores_mov;
            CREATE TEMPORARY TABLE tmp_valores_mov (val_id INT PRIMARY KEY);
            INSERT INTO tmp_valores_mov(val_id)
            SELECT Val_ID FROM valores WHERE Val_FechaAsiento = vfecha AND Val_NumeroAsiento = vorden;

            DROP TEMPORARY TABLE IF EXISTS tmp_entregas_mov;
            CREATE TEMPORARY TABLE tmp_entregas_mov (noe_id INT PRIMARY KEY);
            INSERT INTO tmp_entregas_mov(noe_id)
            SELECT NoE_ID FROM entrega WHERE NoE_MCo_Fecha = vfecha AND NoE_MCo_NroComp = vorden;

            UPDATE valores SET Val_FechaAsiento = NULL, Val_NumeroAsiento = NULL
             WHERE Val_ID IN (SELECT val_id FROM tmp_valores_mov);
            UPDATE entrega SET NoE_MCo_Fecha = NULL, NoE_MCo_NroComp = NULL
             WHERE NoE_ID IN (SELECT noe_id FROM tmp_entregas_mov);

            INSERT INTO asiento (fecha, orden, vinculo, created)
            VALUES (vfecha, vnuevo, vvinculo, NOW());

            UPDATE movcon
               SET MCo_NroComp = vnuevo
             WHERE MCo_Fecha = vfecha AND MCo_NroComp = vorden AND MCo_TCo_ID = vtco;

            SET vdest_val = IF(vtco = 6, vnuevo, vorden);
            SET vdest_ent = IF(vtco = 1, vnuevo, vorden);

            UPDATE valores
               SET Val_FechaAsiento = vfecha, Val_NumeroAsiento = vdest_val
             WHERE Val_ID IN (SELECT val_id FROM tmp_valores_mov);

            UPDATE libro_banco b
              JOIN tmp_valores_mov t ON t.val_id = b.LiB_Val_ID
               SET b.LiB_NumeroAsiento = vdest_val;

            UPDATE entrega
               SET NoE_MCo_Fecha = vfecha, NoE_MCo_NroComp = vdest_ent
             WHERE NoE_ID IN (SELECT noe_id FROM tmp_entregas_mov);

            IF vtco = 3 THEN
                UPDATE ingreso_asiento
                   SET ina_nrocomp = vnuevo
                 WHERE ina_fecha = vfecha AND ina_nrocomp = vorden;
            ELSE
                UPDATE movprov
                   SET MvP_NroComp = vnuevo
                 WHERE MvP_FechaReg = vfecha AND MvP_NroComp = vorden AND MvP_TCo_ID = vtco;
            END IF;
        ELSE
            -- Línea suelta que pertenece al asiento anfitrión: se reetiqueta con
            -- el comprobante dueño. No se mueve nada y no cambia ningún importe.
            UPDATE movcon
               SET MCo_TCo_ID = vowner
             WHERE MCo_Fecha = vfecha AND MCo_NroComp = vorden AND MCo_TCo_ID = vtco;
        END IF;
    END LOOP repara;

    COMMIT;
    CLOSE cur;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- 2. Armado del trabajo (referencias externas, bloques y dueño por asiento)
-- ----------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_refs;
CREATE TEMPORARY TABLE tmp_refs (fecha DATETIME, orden SMALLINT, tco SMALLINT, INDEX idx (fecha, orden));

INSERT INTO tmp_refs(fecha, orden, tco)
SELECT ina_fecha, ina_nrocomp, 3 FROM ingreso_asiento WHERE ina_nrocomp IS NOT NULL
UNION ALL
SELECT Val_FechaAsiento, Val_NumeroAsiento, 6 FROM valores WHERE Val_FechaAsiento IS NOT NULL
UNION ALL
SELECT MvP_FechaReg, MvP_NroComp, MvP_TCo_ID FROM movprov WHERE MvP_FechaReg IS NOT NULL
UNION ALL
SELECT NoE_MCo_Fecha, NoE_MCo_NroComp, 1 FROM entrega WHERE NoE_MCo_Fecha IS NOT NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_grupos;
CREATE TEMPORARY TABLE tmp_grupos (fecha DATETIME, orden SMALLINT, PRIMARY KEY (fecha, orden));

INSERT INTO tmp_grupos(fecha, orden)
SELECT MCo_Fecha, MCo_NroComp
FROM movcon
GROUP BY 1, 2
HAVING COUNT(DISTINCT MCo_TCo_ID) > 1;

DROP TEMPORARY TABLE IF EXISTS tmp_owner;
CREATE TEMPORARY TABLE tmp_owner (fecha DATETIME, orden SMALLINT, owner SMALLINT, PRIMARY KEY (fecha, orden));

INSERT INTO tmp_owner(fecha, orden, owner)
SELECT g.fecha, g.orden,
       COALESCE(
           (SELECT 3 FROM movcon i
             WHERE i.MCo_Fecha = g.fecha AND i.MCo_NroComp = g.orden AND i.MCo_TCo_ID = 3
             LIMIT 1),
           (SELECT MIN(r.tco) FROM tmp_refs r
              JOIN movcon m2 ON m2.MCo_Fecha = r.fecha AND m2.MCo_NroComp = r.orden
                            AND m2.MCo_TCo_ID = r.tco
             WHERE r.fecha = g.fecha AND r.orden = g.orden AND r.tco <> 3),
           (SELECT m4.MCo_TCo_ID FROM movcon m4
             WHERE m4.MCo_Fecha = g.fecha AND m4.MCo_NroComp = g.orden
             GROUP BY m4.MCo_TCo_ID
             ORDER BY COUNT(*) DESC, m4.MCo_TCo_ID
             LIMIT 1)
       )
FROM tmp_grupos g;

DROP TEMPORARY TABLE IF EXISTS tmp_trabajo;
CREATE TEMPORARY TABLE tmp_trabajo AS
SELECT b.fecha, b.orden, b.tco, o.owner, b.lineas, b.saldo
FROM (SELECT m.MCo_Fecha fecha, m.MCo_NroComp orden, m.MCo_TCo_ID tco, COUNT(*) lineas,
             SUM(CASE WHEN m.MCo_Debita = 1 THEN m.MCo_Importe ELSE -m.MCo_Importe END) saldo
      FROM movcon m
      JOIN tmp_grupos g ON g.fecha = m.MCo_Fecha AND g.orden = m.MCo_NroComp
      GROUP BY 1, 2, 3) b
JOIN tmp_owner o ON o.fecha = b.fecha AND o.orden = b.orden;

CALL fix_asientos_mixtos();

-- ----------------------------------------------------------------------------
-- 3. Renumeración de ítems en los asientos que todavía tengan repetidos
-- ----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS renumerar_items_movcon;

DELIMITER $$
CREATE PROCEDURE renumerar_items_movcon()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE vfecha DATETIME;
    DECLARE vorden SMALLINT;

    DECLARE cur CURSOR FOR
        SELECT MCo_Fecha, MCo_NroComp
        FROM movcon
        GROUP BY MCo_Fecha, MCo_NroComp
        HAVING COUNT(*) <> COUNT(DISTINCT MCo_Item);

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;

    renumera: LOOP
        FETCH cur INTO vfecha, vorden;
        IF done = 1 THEN
            LEAVE renumera;
        END IF;

        SET @item_nuevo := 0;
        UPDATE movcon
           SET MCo_Item = (@item_nuevo := @item_nuevo + 1)
         WHERE MCo_Fecha = vfecha AND MCo_NroComp = vorden
         ORDER BY MCo_Item, id;
    END LOOP renumera;

    CLOSE cur;
END$$
DELIMITER ;

CALL renumerar_items_movcon();

DROP TEMPORARY TABLE IF EXISTS tmp_trabajo;
DROP TEMPORARY TABLE IF EXISTS tmp_owner;
DROP TEMPORARY TABLE IF EXISTS tmp_grupos;
DROP TEMPORARY TABLE IF EXISTS tmp_refs;

-- ----------------------------------------------------------------------------
-- 4. Verificación (todo debe dar 0)
-- ----------------------------------------------------------------------------
SELECT 'asientos_mixtos_restantes' AS control, COUNT(*) AS valor
FROM (SELECT MCo_Fecha, MCo_NroComp FROM movcon
      GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) x
UNION ALL
SELECT 'items_repetidos_restantes', COUNT(*)
FROM (SELECT 1 FROM movcon GROUP BY MCo_Fecha, MCo_NroComp, MCo_Item HAVING COUNT(*) > 1) y
UNION ALL
SELECT 'asientos_descuadrados', COUNT(*)
FROM (SELECT MCo_Fecha, MCo_NroComp FROM movcon
      GROUP BY 1, 2
      HAVING SUM(CASE WHEN MCo_Debita = 1 THEN MCo_Importe ELSE -MCo_Importe END) <> 0) z;

-- Asientos que quedan descuadrados: hay que revisarlos a mano (son previos a
-- esta reparación; el script no los toca).
SELECT MCo_Fecha, MCo_NroComp,
       SUM(CASE WHEN MCo_Debita = 1 THEN MCo_Importe ELSE 0 END) AS debe,
       SUM(CASE WHEN MCo_Debita = 0 THEN MCo_Importe ELSE 0 END) AS haber,
       COUNT(*) AS lineas
FROM movcon
GROUP BY 1, 2
HAVING debe <> haber
ORDER BY MCo_Fecha;

-- ----------------------------------------------------------------------------
-- 5. Índice único (idempotente)
-- ----------------------------------------------------------------------------
SET @existe := (SELECT COUNT(*) FROM information_schema.statistics
                WHERE table_schema = DATABASE() AND table_name = 'movcon'
                  AND index_name = 'uk_movcon_fecha_nrocomp_item');

SET @sql := IF(@existe = 0,
    'ALTER TABLE movcon ADD UNIQUE INDEX uk_movcon_fecha_nrocomp_item (MCo_Fecha, MCo_NroComp, MCo_Item)',
    'SELECT ''el indice uk_movcon_fecha_nrocomp_item ya existe'' AS mensaje');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------------------------------------------------------
-- 6. Limpieza: el script no deja objetos propios en la base
-- ----------------------------------------------------------------------------
-- Elimina las tablas bk_* y los procedimientos de trabajo, para que la base
-- quede exactamente como una base normal. La limpieza es condicional: si la
-- verificación de arriba no dio 0 / 0, los bk_* se conservan para poder correr
-- rollback_V433.sql.
--
-- Para auditar el antes/después, comentar el `CALL limpiar_v433();` y las
-- sentencias DROP de abajo.

DROP PROCEDURE IF EXISTS limpiar_v433;

DELIMITER $$
CREATE PROCEDURE limpiar_v433()
BEGIN
    DECLARE vmixtos INT;
    DECLARE vitems INT;

    SELECT COUNT(*) INTO vmixtos
    FROM (SELECT MCo_Fecha, MCo_NroComp FROM movcon
          GROUP BY 1, 2 HAVING COUNT(DISTINCT MCo_TCo_ID) > 1) x;

    SELECT COUNT(*) INTO vitems
    FROM (SELECT 1 FROM movcon
          GROUP BY MCo_Fecha, MCo_NroComp, MCo_Item HAVING COUNT(*) > 1) y;

    IF vmixtos = 0 AND vitems = 0 THEN
        DROP TABLE IF EXISTS bk_asientos_mixtos, bk_movcon_mixtos, bk_asiento_mixtos,
            bk_valores_mixtos, bk_movprov_mixtos, bk_entrega_mixtos,
            bk_ingreso_asiento_mixtos, bk_libro_banco_mixtos;
        SELECT 'limpieza_ok: se eliminaron los bk_* de la reparacion' AS mensaje;
    ELSE
        SELECT 'limpieza_omitida: quedan asientos mixtos o items repetidos; se conservan los bk_*' AS mensaje;
    END IF;
END$$
DELIMITER ;

CALL limpiar_v433();

DROP PROCEDURE IF EXISTS limpiar_v433;
DROP PROCEDURE IF EXISTS fix_asientos_mixtos;
DROP PROCEDURE IF EXISTS renumerar_items_movcon;

