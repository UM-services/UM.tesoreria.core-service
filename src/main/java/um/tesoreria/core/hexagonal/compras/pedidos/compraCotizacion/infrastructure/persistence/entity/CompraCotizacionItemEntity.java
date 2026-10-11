package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "compra_cotizacion_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraCotizacionItemEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_cotizacion_item_id")
    private Long compraCotizacionItemId;

    @Column(name = "compra_cotizacion_id", nullable = false)
    private Long compraCotizacionId;

    @Column(name = "compra_pedido_item_id", nullable = false)
    private Integer compraPedidoItemId;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;
}
