package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.math.BigDecimal;
import java.util.List;

/** Un producto (Matrícula, Arancel, ...) con el título y la cantidad de cuotas de su alternativa. */
public record ProductoEstado(
        Integer productoId,
        String nombre,
        String tituloCuota,
        Integer totalCuotas,
        BigDecimal total,
        BigDecimal pagado,
        List<CuotaEstado> cuotas) {
}