package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Orden de Compra con recepción pendiente (bandeja de recepción). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionPendienteResponse {

    private Long compraFormalizacionId;
    private String numero;
    private String numeroExterno;
    private Integer ejercicioId;
    private Integer proveedorId;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private String moneda;
    private List<CompraRecepcionPendienteItemResponse> items;
    private List<CompraRecepcionResponse> recepciones;
    private Boolean completo;

}
