package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.dto.CompraPedidoItemRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Alta/edición de un pedido de compra.
 *
 * <p>Los identificadores de solicitante, dependencia, facultad y sede llegan desde la
 * sesión del usuario logueado (los arma el cliente). El número no se envía: lo asigna el
 * sistema al enviar.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoRequest {

    private Integer ejercicioId;
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
    private List<CompraPedidoItemRequest> items;

}
