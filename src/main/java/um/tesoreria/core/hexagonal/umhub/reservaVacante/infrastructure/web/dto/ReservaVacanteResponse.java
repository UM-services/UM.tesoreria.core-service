package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto;

import lombok.*;

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
    private BigDecimal importe;
    private OffsetDateTime vencimiento;
    private LocalDateTime created;
    private String estado;
    private LocalDateTime updated;
}
