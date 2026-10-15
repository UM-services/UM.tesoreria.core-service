package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Ítem recibido de una entrega (cantidad y resultado por ítem de la OC). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionItemRequest {

    /** Ítem contratado de la Orden de Compra. */
    private Long compraFormalizacionItemId;
    private Integer orden;
    private BigDecimal cantidadRecibida;
    /** {@code CONFORME} | {@code CON_OBSERVACIONES} | {@code NO_CONFORME}. */
    private String resultado;
    private String observacion;

}
