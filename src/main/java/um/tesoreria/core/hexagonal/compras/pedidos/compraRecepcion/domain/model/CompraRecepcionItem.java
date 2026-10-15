package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Detalle de la recepción: lo recibido por ítem de la Orden de Compra
 * ({@code compraFormalizacionItemId}, snapshot contratado) con su resultado.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionItem {

    private Long compraRecepcionItemId;
    private Long compraRecepcionId;
    /** Ítem contratado de la OC: trazabilidad y base del saldo. */
    private Long compraFormalizacionItemId;
    private Integer orden;
    private BigDecimal cantidadRecibida;
    private CompraRecepcionResultado resultado;
    private String observacion;

}
