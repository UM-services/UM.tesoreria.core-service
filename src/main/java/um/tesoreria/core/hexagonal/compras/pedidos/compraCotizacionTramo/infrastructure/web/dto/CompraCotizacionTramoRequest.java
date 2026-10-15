package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacionTramoRequest {

    private String nombre;

    /** Tope en múltiplos de la referencia; nulo = tramo sin tope (el último). */
    private Integer topeMultiplico;

    private Integer cantidadMinima;

    private Byte activo;

}
