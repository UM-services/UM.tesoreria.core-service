package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class
MercadoPagoContextHistory {

    private UUID mercadoPagoContextHistoryId;
    private Long mercadoPagoContextId;
    private Long chequeraCuotaId;
    private String initPoint;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaVencimiento;

    @Builder.Default
    private BigDecimal importe = BigDecimal.ZERO;

    @Builder.Default
    private Byte changed = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime lastVencimientoUpdated;

    private String preferenceId;
    private String preference;

    @Builder.Default
    private Byte activo = 0;
    private Long chequeraPagoId;
    private String idMercadoPago;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaPago;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaAcreditacion;

    @Builder.Default
    private BigDecimal importePagado = BigDecimal.ZERO;
    private String payment;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
