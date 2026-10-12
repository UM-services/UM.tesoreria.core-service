package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Alta de la Orden de Compra (POST). El proveedor, el monto y las condiciones se snapshotean de la
 * oferta adjudicada; los campos aquí presentes los sobrescriben cuando vienen informados. Los ítems
 * del instrumento se toman siempre del snapshot del proceso y la oferta adjudicada.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFormalizacionRequest {

    private Long compraCotizacionId;
    private String numeroExterno;
    private String moneda;
    private BigDecimal monto;
    private String condicionPago;
    private String plazoEntrega;
    private String garantia;
    private String objeto;
    private String observacion;
    /** Compras: usuario que formaliza (actor del instrumento). */
    private Integer usuarioId;

}
