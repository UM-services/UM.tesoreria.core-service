package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoContextRequest {

    private Long chequeraCuotaId;
    private String initPoint;
    private OffsetDateTime fechaVencimiento;
    private BigDecimal importe;
    private Byte changed;
    private OffsetDateTime lastVencimientoUpdated;
    private String preferenceId;
    private String preference;
    private Byte activo;
    private Long chequeraPagoId;
    private String idMercadoPago;
    private String status;
    private OffsetDateTime fechaPago;
    private OffsetDateTime fechaAcreditacion;
    private BigDecimal importePagado;
    private String payment;

}
