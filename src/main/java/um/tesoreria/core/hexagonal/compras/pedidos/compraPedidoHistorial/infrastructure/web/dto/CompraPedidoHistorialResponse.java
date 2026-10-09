package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoHistorialResponse {

    private Long compraPedidoHistorialId;
    private Integer compraPedidoId;
    private String estado;
    private Integer usuarioId;
    private String observacion;
    private LocalDateTime fecha;

}
