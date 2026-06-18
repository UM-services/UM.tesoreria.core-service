package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.entity.ReservaVacanteEntity;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "mercado_pago_context")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MercadoPagoContextEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mercadoPagoContextId;

    private Long chequeraCuotaId;
    private UUID reservaVacanteId;
    private String initPoint;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaVencimiento;

    @Builder.Default
    private BigDecimal importe = BigDecimal.ZERO;

    @Builder.Default
    private Byte changed = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime lastVencimientoUpdated;

    private String preferenceId;
    private String preference;

    @Builder.Default
    private Byte activo = 0;

    private Long chequeraPagoId;
    private String idMercadoPago;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaPago;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaAcreditacion;

    @Builder.Default
    private BigDecimal importePagado = BigDecimal.ZERO;

    private String payment;

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraCuotaId", referencedColumnName = "chc_id", insertable = false, updatable = false)
    private ChequeraCuotaEntity chequeraCuota;

    @OneToOne(optional = true)
    @JoinColumn(name  = "reservaVacanteId", insertable = false, updatable = false)
    private ReservaVacanteEntity reservaVacante;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
