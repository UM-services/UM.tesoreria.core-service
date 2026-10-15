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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_formalizacion")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraFormalizacionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_formalizacion_id")
    private Long compraFormalizacionId;

    @Column(name = "compra_adjudicacion_id", nullable = false)
    private Long compraAdjudicacionId;

    @Column(name = "numero", nullable = false, length = 16)
    private String numero;

    @Column(name = "numero_externo", length = 40)
    private String numeroExterno;

    @Column(name = "ejercicio_id", nullable = false)
    private Integer ejercicioId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "proveedor_id", nullable = false)
    private Integer proveedorId;

    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "monto", precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(name = "condicion_pago", length = 100)
    private String condicionPago;

    @Column(name = "plazo_entrega", length = 100)
    private String plazoEntrega;

    @Column(name = "garantia", length = 100)
    private String garantia;

    @Column(name = "objeto", columnDefinition = "text")
    private String objeto;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

}
