package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoContext {

    private Long mercadoPagoContextId;
    private Long chequeraCuotaId;
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

}
