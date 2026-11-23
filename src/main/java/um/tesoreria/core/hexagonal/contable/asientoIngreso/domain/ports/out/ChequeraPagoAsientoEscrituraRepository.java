package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.LineaChequeraPagoAsiento;

import java.time.OffsetDateTime;
import java.util.List;

/** Escritura de la tabla intermedia {@code chequera_pago_asiento} que consume {@code /pago/items}. */
public interface ChequeraPagoAsientoEscrituraRepository {

    /**
     * Baja todas las líneas del día/tipo antes de regenerarlas. Es un borrado por alcance (no por
     * pago): además de ser una sola operación en lugar de una por pago, limpia filas huérfanas de
     * pagos que ya no existen y las que quedaron con hora (Mercado Pago previo al 2026-11-21), que
     * {@code /pago/items} no suma pero un reporte del legacy sí.
     */
    void deleteAllByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId);

    /** Inserta todas las líneas del asiento. */
    void createAll(List<LineaChequeraPagoAsiento> lineas);

    /**
     * Fuerza el flush del contexto de persistencia. Hibernate ejecuta los INSERT antes que los
     * DELETE, así que las bajas encoladas del asiento y de {@code movcon} tienen que ejecutarse
     * antes de regenerar las líneas.
     */
    void flush();

}
