-- V431__contable_asiento_secuencia.sql
-- Secuencia autoritativa del orden contable por fecha.
--
-- El orden contable es único por fecha (asiento UNIQUE(fecha, orden)), pero se asignaba con
-- 1 + MAX(movcon.mco_nrocomp), es decir, leyendo el resultado y no las reservas. Un proceso podía
-- reservar un número (ingreso_asiento, valores.Val_NumeroAsiento, movprov.MvP_NroComp) y escribir
-- sus líneas varios segundos después; en esa ventana otro proceso obtenía el mismo número.
--
-- Esta tabla es el registro de reservas: se bloquea la fila de la fecha (SELECT ... FOR UPDATE) y
-- se entrega ultimo_orden + 1 antes de escribir movcon.

CREATE TABLE IF NOT EXISTS contable_asiento_secuencia (
    fecha        DATETIME(6) NOT NULL,
    ultimo_orden SMALLINT    NOT NULL DEFAULT 0,
    created      DATETIME(6) NOT NULL,
    updated      DATETIME(6) NOT NULL,
    PRIMARY KEY (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- La tabla se siembra por fecha en la primera reserva con
-- GREATEST(max(asiento.orden), max(movcon.mco_nrocomp),
--          max(ingreso_asiento.ina_nrocomp), max(valores.Val_NumeroAsiento)).
-- No hace falta un backfill masivo.
