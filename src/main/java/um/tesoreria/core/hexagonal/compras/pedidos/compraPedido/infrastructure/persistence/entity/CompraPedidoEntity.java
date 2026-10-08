package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_pedido")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_pedido_id")
    private Integer compraPedidoId;

    @Column(name = "numero", length = 16)
    private String numero;

    @Column(name = "ejercicio_id", nullable = false)
    private Integer ejercicioId;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "autorizante_id")
    private Integer autorizanteId;

    @Column(name = "solicitante_id", nullable = false)
    private Integer solicitanteId;

    @Column(name = "dependencia_id", nullable = false)
    private Integer dependenciaId;

    @Column(name = "facultad_id")
    private Integer facultadId;

    @Column(name = "geografica_id")
    private Integer geograficaId;

    @Column(name = "necesidad", length = 65535)
    private String necesidad;

    @Column(name = "fecha_requerida")
    private LocalDateTime fechaRequerida;

    @Builder.Default
    @Column(name = "urgente", nullable = false)
    private Boolean urgente = false;

    @Column(name = "urgencia_motivo", length = 255)
    private String urgenciaMotivo;

    @Builder.Default
    @Column(name = "monto_conocido", nullable = false)
    private Boolean montoConocido = false;

    @Column(name = "monto_estimado", precision = 19, scale = 2)
    private BigDecimal montoEstimado;

    @Column(name = "fuente_estimacion", length = 255)
    private String fuenteEstimacion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "rechazo_motivo", length = 500)
    private String rechazoMotivo;

    @Column(name = "descartado_motivo", length = 500)
    private String descartadoMotivo;

}
