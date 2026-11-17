package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.PagoIngreso;

import java.math.BigDecimal;

/** Escritura de la tabla intermedia {@code chequera_pago_asiento} que consume {@code /pago/items}. */
public interface ChequeraPagoAsientoEscrituraRepository {

    void deleteAllByPago(PagoIngreso pago);

    void create(PagoIngreso pago, Integer item, BigDecimal cuenta, Byte debita, BigDecimal importe);

    /**
     * Fuerza el flush del contexto de persistencia. Hibernate ejecuta los INSERT antes que los
     * DELETE, así que las bajas encoladas (de esta tabla y del asiento) tienen que ejecutarse
     * antes de regenerar las líneas.
     */
    void flush();

}
