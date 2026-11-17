package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Estado actual del asiento de ingreso de una fecha y un tipo de pago. {@code existe} indica si
 * hay una fila en {@code ingreso_asiento}; si no la hay, el legacy muestra totales en cero.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoResumen {

    private OffsetDateTime fecha;
    private Integer tipoPagoId;
    private String tipoPagoNombre;
    private boolean existe;
    private Integer ordenContable;
    private String vinculo;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;
    private boolean cuadra;
    private List<AsientoIngresoLinea> lineas;

}
