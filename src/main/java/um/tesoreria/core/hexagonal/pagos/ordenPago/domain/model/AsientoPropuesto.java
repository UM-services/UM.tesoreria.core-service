package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Asiento propuesto. Si {@code ordenContable} es {@code null}, el asiento todavía no existe
 * y quien persiste debe reservar el orden con el asignador.
 *
 * <p>{@code origenes} identifica a los comprobantes cuyas líneas componen el asiento; puede
 * haber más de uno cuando el legacy agrupó dos hijos en la misma fecha y orden.</p>
 */
public record AsientoPropuesto(OffsetDateTime fechaContable, Integer ordenContable,
                               List<OrigenAsiento> origenes, List<LineaPropuesta> lineas) {

    public BigDecimal totalDebe() {
        return total((byte) 1);
    }

    public BigDecimal totalHaber() {
        return total((byte) 0);
    }

    public boolean cuadra() {
        return totalDebe().compareTo(totalHaber()) == 0;
    }

    public boolean sinOrden() {
        return ordenContable == null || ordenContable == 0;
    }

    public AsientoPropuesto conOrden(Integer orden) {
        return new AsientoPropuesto(fechaContable, orden, origenes, lineas);
    }

    private BigDecimal total(byte debita) {
        return lineas.stream()
                .filter(linea -> linea.debita() != null && linea.debita() == debita)
                .map(LineaPropuesta::importe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
