package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Un producto (Matrícula, Arancel, ...) con el título y la cantidad de cuotas de su alternativa.
 * {@code deuda} es {@code total - pagado}, ya resuelta por este servicio.
 */
public record ProductoEstado(
        Integer productoId,
        String nombre,
        String tituloCuota,
        Integer totalCuotas,
        BigDecimal total,
        BigDecimal pagado,
        BigDecimal deuda,
        List<CuotaEstado> cuotas) {
}