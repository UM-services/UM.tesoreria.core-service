package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Precio/condición de un proveedor para un ítem del proceso (detalle de la respuesta). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacionProveedorItem {

    private Long compraCotizacionProveedorItemId;
    private Long compraCotizacionProveedorId;
    private Long compraCotizacionItemId;
    @Builder.Default
    private Boolean cotiza = true;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String marca;
    private String observacion;
}
