package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_cotizacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraCotizacionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_cotizacion_id")
    private Long compraCotizacionId;

    @Column(name = "numero", length = 16)
    private String numero;

    @Column(name = "ejercicio_id", nullable = false)
    private Integer ejercicioId;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "fecha_limite_respuesta")
    private LocalDateTime fechaLimiteRespuesta;

    @Column(name = "monto_estimado", precision = 19, scale = 2)
    private BigDecimal montoEstimado;

    @Column(name = "cantidad_minima")
    private Integer cantidadMinima;

    @Column(name = "excepcion_motivo", length = 500)
    private String excepcionMotivo;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "proveedor_recomendado_id")
    private Long proveedorRecomendadoId;

    @Column(name = "fundamento_recomendacion", length = 1000)
    private String fundamentoRecomendacion;

    @Column(name = "fecha_recomendacion")
    private LocalDateTime fechaRecomendacion;

    @Column(name = "usuario_id")
    private Integer usuarioId;
}
