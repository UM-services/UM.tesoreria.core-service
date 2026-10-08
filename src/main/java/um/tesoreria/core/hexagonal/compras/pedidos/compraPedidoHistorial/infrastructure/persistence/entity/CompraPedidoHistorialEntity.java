package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_pedido_historial")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_pedido_historial_id")
    private Long compraPedidoHistorialId;

    @Column(name = "compra_pedido_id", nullable = false)
    private Integer compraPedidoId;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

}
