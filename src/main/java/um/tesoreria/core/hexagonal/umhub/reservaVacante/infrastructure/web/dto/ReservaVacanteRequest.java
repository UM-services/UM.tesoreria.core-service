package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReservaVacanteRequest {

    private Integer tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellido;
    private String email;
    private UUID campanhaId;

}
