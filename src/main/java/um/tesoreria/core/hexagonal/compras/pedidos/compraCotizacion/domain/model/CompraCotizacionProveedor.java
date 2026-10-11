package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de cotización dirigido a un proveedor (documento por proveedor). Se envía
 * (email snapshot + fecha); la respuesta (totales + precios por ítem) se carga en la etapa 6C.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacionProveedor {

    private Long compraCotizacionProveedorId;
    private Long compraCotizacionId;
    private Integer proveedorId;
    private String email;
    @Builder.Default
    private CompraCotizacionProveedorEstado estado = CompraCotizacionProveedorEstado.PENDIENTE;
    private LocalDateTime fechaEnvio;
    private String observacion;
    // Respuesta (etapa 6C)
    private LocalDateTime fechaRespuesta;
    private String moneda;
    private BigDecimal total;
    private String plazoEntrega;
    private String condicionPago;
    private String garantia;
    private String validezOferta;
    @Builder.Default
    private List<CompraCotizacionProveedorItem> items = new ArrayList<>();

    /** Carga la respuesta del proveedor (etapa 6C). */
    public void registrarRespuesta(LocalDateTime fechaRespuesta, String moneda, BigDecimal total,
                                   String plazoEntrega, String condicionPago, String garantia, String validezOferta) {
        if (estado == CompraCotizacionProveedorEstado.ANULADA) {
            throw new IllegalStateException("El documento del proveedor está anulado");
        }
        this.estado = CompraCotizacionProveedorEstado.RESPONDIDA;
        this.fechaRespuesta = fechaRespuesta != null ? fechaRespuesta : LocalDateTime.now();
        this.moneda = moneda;
        this.total = total;
        this.plazoEntrega = plazoEntrega;
        this.condicionPago = condicionPago;
        this.garantia = garantia;
        this.validezOferta = validezOferta;
    }

    public void marcarNoCotiza(String observacion) {
        this.estado = CompraCotizacionProveedorEstado.NO_COTIZA;
        if (observacion != null && !observacion.isBlank()) {
            this.observacion = observacion;
        }
    }

    public boolean esRespondida() {
        return estado == CompraCotizacionProveedorEstado.RESPONDIDA;
    }
}
