package um.tesoreria.core.hexagonal.lectivo.infrastructure.web.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class LectivoRequest {
    private Integer lectivoId;
    private String nombre;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinal;
}
