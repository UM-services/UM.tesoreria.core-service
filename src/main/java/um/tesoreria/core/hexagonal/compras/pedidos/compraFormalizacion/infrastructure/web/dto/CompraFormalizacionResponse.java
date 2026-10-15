package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFormalizacionResponse {

    private Long compraFormalizacionId;
    private Long compraAdjudicacionId;
    private String numero;
    private String numeroExterno;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private Integer proveedorId;
    private String moneda;
    private BigDecimal monto;
    private String condicionPago;
    private String plazoEntrega;
    private String garantia;
    private String objeto;
    private String observacion;
    private Integer usuarioId;
    private List<CompraFormalizacionItemResponse> items;
    private LocalDateTime created;
    private LocalDateTime updated;

}
