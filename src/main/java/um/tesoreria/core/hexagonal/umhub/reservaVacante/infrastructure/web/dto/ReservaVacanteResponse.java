package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto;

import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime created;
    private String estado;
    private LocalDateTime updated;
}
