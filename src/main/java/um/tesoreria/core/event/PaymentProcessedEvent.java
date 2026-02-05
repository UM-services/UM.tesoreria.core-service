package um.tesoreria.core.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentProcessedEvent {
    private Long mercadoPagoContextId;
    private Long chequeraCuotaId;
    private String paymentId;
    private String status;
    private String statusDetail;
    private OffsetDateTime dateApproved;
    private OffsetDateTime dateCreated;
    private BigDecimal transactionAmount;
    private String paymentJson;
}
