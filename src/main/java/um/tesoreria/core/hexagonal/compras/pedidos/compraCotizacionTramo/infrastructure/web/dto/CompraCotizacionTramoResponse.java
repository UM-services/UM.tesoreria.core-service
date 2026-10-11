package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacionTramoResponse {

    private Long cotizacionTramoId;
    private String nombre;
    private Integer topeMultiplico;
    private Integer cantidadMinima;
    /** Derivado: true cuando el tramo no tiene tope (cubre el resto de los montos). */
    private Boolean abierto;
    private Byte activo;
    private LocalDateTime created;
    private LocalDateTime updated;

}
