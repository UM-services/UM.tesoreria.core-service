package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_cotizacion_proveedor")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraCotizacionProveedorEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_cotizacion_proveedor_id")
    private Long compraCotizacionProveedorId;

    @Column(name = "compra_cotizacion_id", nullable = false)
    private Long compraCotizacionId;

    @Column(name = "proveedor_id", nullable = false)
    private Integer proveedorId;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "total", precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "plazo_entrega", length = 100)
    private String plazoEntrega;

    @Column(name = "condicion_pago", length = 100)
    private String condicionPago;

    @Column(name = "garantia", length = 100)
    private String garantia;

    @Column(name = "validez_oferta", length = 100)
    private String validezOferta;
}
