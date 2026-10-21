package um.tesoreria.core.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentProcessedEvent {
    private Long mercadoPagoContextId;
    private Long chequeraCuotaId;
    private UUID reservaVacanteId;
    private String paymentId;
    private String status;
    private String statusDetail;
    private OffsetDateTime dateApproved;
    private OffsetDateTime dateCreated;
    /** {@code date_last_updated} del pago en MP: ordena los eventos de un mismo pago. */
    private OffsetDateTime dateLastUpdated;
    private BigDecimal transactionAmount;
    private String paymentJson;
    /** {@code WEBHOOK_V1} | {@code IPN_LEGACY} | {@code MANUAL}. */
    private String source;
}
