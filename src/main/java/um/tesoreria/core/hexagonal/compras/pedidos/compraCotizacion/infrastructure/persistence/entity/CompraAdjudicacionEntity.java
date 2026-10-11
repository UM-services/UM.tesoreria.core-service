package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_adjudicacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraAdjudicacionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_adjudicacion_id")
    private Long compraAdjudicacionId;

    @Column(name = "compra_cotizacion_id", nullable = false)
    private Long compraCotizacionId;

    @Column(name = "ejercicio_id")
    private Integer ejercicioId;

    @Column(name = "compra_cotizacion_proveedor_id")
    private Long compraCotizacionProveedorId;

    @Column(name = "proveedor_adjudicado_id")
    private Integer proveedorAdjudicadoId;

    @Column(name = "monto", precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(name = "decision", nullable = false, length = 20)
    private String decision;

    @Column(name = "fundamento", length = 1000)
    private String fundamento;

    @Column(name = "autoridad_usuario_id", nullable = false)
    private Long autoridadUsuarioId;

    @Column(name = "limite_aplicado", precision = 19, scale = 2)
    private BigDecimal limiteAplicado;

    @Column(name = "fecha")
    private LocalDateTime fecha;
}
