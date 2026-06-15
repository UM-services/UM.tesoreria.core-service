package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.dto;

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
public class MercadoPagoContextResponse {

    private Long mercadoPagoContextId;
    private Long chequeraCuotaId;
    private UUID reservaVacanteId;
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

    private ChequeraCuota chequeraCuota;
    private ReservaVacante reservaVacante;

}
