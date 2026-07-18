package um.tesoreria.core.hexagonal.lectivo.infrastructure.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class LectivoRequest {
    private Integer lectivoId;
    private String nombre;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinal;
}
