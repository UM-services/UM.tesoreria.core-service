package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Tramo de cotizaciones mínimas: fija un tope y la cantidad mínima de cotizaciones a solicitar
 * para un pedido cuyo monto estimado no supere ese tope.
 *
 * <p>El tope ({@code topeMultiplico}) se expresa como <b>múltiplo de la referencia del ejercicio</b>
 * ({@code compra_referencia}); {@code null} significa <b>sin tope</b> (último tramo). La resolución
 * del mínimo para un monto es responsabilidad de una etapa posterior.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacionTramo {

    private Long cotizacionTramoId;
    private String nombre;
    private Integer topeMultiplico;
    private Integer cantidadMinima;
    @Builder.Default
    private Byte activo = 1;
    private LocalDateTime created;
    private LocalDateTime updated;

    /** {@code true} cuando el tramo no tiene tope (cubre el resto de los montos). */
    public boolean esAbierto() {
        return topeMultiplico == null;
    }
}
