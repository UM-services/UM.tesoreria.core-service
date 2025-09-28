package um.tesoreria.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MercadoPagoContext extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mercadoPagoContextId;
    
    private Long chequeraCuotaId;
    private String initPoint;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaVencimiento;
    
    private BigDecimal importe = BigDecimal.ZERO;
    private Byte changed = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime lastVencimientoUpdated;

    private String preferenceId;
    private String preference;
    private Byte activo = 0;
    private Long chequeraPagoId;
    private String idMercadoPago;
    private String status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaPago;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaAcreditacion;
    
    private BigDecimal importePagado = BigDecimal.ZERO;
    private String payment;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
