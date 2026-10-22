package um.tesoreria.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Datos que el barrido de renovaciones puede persistir de un contexto.
 *
 * <p>Solo incluye campos de la preferencia de Mercado Pago: nunca datos de pago
 * ({@code status}, {@code importePagado}, {@code fechaPago}, {@code idMercadoPago}), para no
 * pisar un pago acreditado durante la corrida.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaContextPatchDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaVencimiento;

    private BigDecimal importe;
    private String initPoint;
    private String preferenceId;
    private String preference;
    private Byte changed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime lastVencimientoUpdated;

}
