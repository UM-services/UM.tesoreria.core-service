package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoContext {

    private Long mercadoPagoContextId;
    private Long chequeraCuotaId;
    private UUID reservaVacanteId;
    private String initPoint;
    private OffsetDateTime fechaVencimiento;

    @Builder.Default
    private BigDecimal importe = BigDecimal.ZERO;

    @Builder.Default
    private Byte changed = 0;

    private OffsetDateTime lastVencimientoUpdated;
    private String preferenceId;
    private String preference;

    @Builder.Default
    private Byte activo = 0;

    private Long chequeraPagoId;
    private String idMercadoPago;
    private String status;
    private OffsetDateTime fechaPago;
    private OffsetDateTime fechaAcreditacion;

    @Builder.Default
    private BigDecimal importePagado = BigDecimal.ZERO;

    private String payment;

    private ChequeraCuota chequeraCuota;
    private ReservaVacante reservaVacante;

}
