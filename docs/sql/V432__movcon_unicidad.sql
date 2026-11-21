-- V432__movcon_unicidad.sql
-- CuentaMovimientoEntity ya declara @UniqueConstraint(mco_fecha, mco_nrocomp, mco_item), pero el
-- esquema real sólo tiene un KEY común: por eso dos asientos podían compartir (fecha, orden, item)
-- y mezclarse en silencio.
--
-- Antes de crear el índice hay que resolver los duplicados que dejó la numeración compartida
-- (ingreso + OP/factura en el mismo orden). Criterio: se conserva la fila de menor id de cada
-- (fecha, nrocomp, item) y las sobrantes se renumeran al final del mismo asiento. No se borran
-- líneas ni se alteran importes.

CREATE TEMPORARY TABLE movcon_max_item ENGINE=InnoDB AS
SELECT MCo_Fecha, MCo_NroComp, MAX(MCo_Item) AS max_item
  FROM movcon
 GROUP BY MCo_Fecha, MCo_NroComp;

CREATE TEMPORARY TABLE movcon_duplicado_extra (
    seq     INT NOT NULL AUTO_INCREMENT,
    id      INT NOT NULL,
    fecha   DATETIME NOT NULL,
    nrocomp SMALLINT NOT NULL,
    PRIMARY KEY (seq),
    UNIQUE KEY uk_id (id)
) ENGINE=InnoDB;

INSERT INTO movcon_duplicado_extra (id, fecha, nrocomp)
SELECT m.id, m.MCo_Fecha, m.MCo_NroComp
  FROM movcon m
  JOIN (SELECT MCo_Fecha, MCo_NroComp, MCo_Item, MIN(id) AS keep_id
          FROM movcon
         GROUP BY MCo_Fecha, MCo_NroComp, MCo_Item
        HAVING COUNT(*) > 1) d
    ON d.MCo_Fecha = m.MCo_Fecha
   AND d.MCo_NroComp = m.MCo_NroComp
   AND d.MCo_Item = m.MCo_Item
 WHERE m.id <> d.keep_id;

UPDATE movcon m
  JOIN movcon_duplicado_extra x ON x.id = m.id
  JOIN movcon_max_item t ON t.MCo_Fecha = x.fecha AND t.MCo_NroComp = x.nrocomp
   SET m.MCo_Item = t.max_item + x.seq;

DROP TEMPORARY TABLE movcon_duplicado_extra;
DROP TEMPORARY TABLE movcon_max_item;

ALTER TABLE movcon
    ADD UNIQUE KEY uk_movcon_fecha_nrocomp_item (MCo_Fecha, MCo_NroComp, MCo_Item);
