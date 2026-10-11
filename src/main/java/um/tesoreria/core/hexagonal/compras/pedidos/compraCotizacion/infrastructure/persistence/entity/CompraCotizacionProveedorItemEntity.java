package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "compra_cotizacion_proveedor_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraCotizacionProveedorItemEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_cotizacion_proveedor_item_id")
    private Long compraCotizacionProveedorItemId;

    @Column(name = "compra_cotizacion_proveedor_id", nullable = false)
    private Long compraCotizacionProveedorId;

    @Column(name = "compra_cotizacion_item_id", nullable = false)
    private Long compraCotizacionItemId;

    @Column(name = "cotiza", nullable = false)
    private Boolean cotiza;

    @Column(name = "precio_unitario", precision = 19, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "marca", length = 150)
    private String marca;

    @Column(name = "observacion", length = 500)
    private String observacion;
}
