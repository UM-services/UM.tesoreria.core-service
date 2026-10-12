package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "compra_formalizacion_item")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraFormalizacionItemEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_formalizacion_item_id")
    private Long compraFormalizacionItemId;

    @Column(name = "compra_formalizacion_id", nullable = false)
    private Long compraFormalizacionId;

    @Column(name = "compra_cotizacion_item_id")
    private Long compraCotizacionItemId;

    @Column(name = "compra_pedido_item_id")
    private Integer compraPedidoItemId;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "unidad", length = 50)
    private String unidad;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "especificaciones", columnDefinition = "text")
    private String especificaciones;

    @Column(name = "precio_unitario", precision = 19, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "marca", length = 150)
    private String marca;

}
