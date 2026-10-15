package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

/**
 * Detalle de un pedido de compra. La unidad y la descripción son texto libre.
 */
@Getter
@Setter
@Entity
@Table(name = "compra_pedido_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoItemEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_pedido_item_id")
    private Integer compraPedidoItemId;

    @Column(name = "compra_pedido_id", nullable = false)
    private Integer compraPedidoId;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "unidad", length = 50)
    private String unidad;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "especificaciones", length = 65535)
    private String especificaciones;

    @Column(name = "referencia_web", length = 500)
    private String referenciaWeb;

}
