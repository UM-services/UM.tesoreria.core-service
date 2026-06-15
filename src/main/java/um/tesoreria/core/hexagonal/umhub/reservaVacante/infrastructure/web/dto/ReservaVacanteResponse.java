package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaVacanteResponse {
    private UUID reservaVacanteId;
    private Integer tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellido;
    private String email;
    private UUID campanhaId;
    private String estado;
    private BigDecimal importe;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento;
    private String initPoint;
    private LocalDateTime created;
    private LocalDateTime updated;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
