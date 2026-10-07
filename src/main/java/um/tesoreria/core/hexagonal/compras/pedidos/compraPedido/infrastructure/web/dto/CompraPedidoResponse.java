package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.dto.CompraPedidoItemResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoResponse {

    private Integer compraPedidoId;
    private String numero;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private String estado;
    private Integer autorizanteId;
    private Integer solicitanteId;
    private Integer dependenciaId;
    private Integer facultadId;
    private Integer geograficaId;
    private String necesidad;
    private LocalDateTime fechaRequerida;
    private Boolean urgente;
    private String urgenciaMotivo;
    private Boolean montoConocido;
    private BigDecimal montoEstimado;
    private String fuenteEstimacion;
    private List<CompraPedidoItemResponse> items;

}
