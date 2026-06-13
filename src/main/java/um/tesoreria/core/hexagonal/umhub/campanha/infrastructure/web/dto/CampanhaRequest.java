package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CampanhaRequest {
    private UUID campanhaId;
    private String nombre;
    private Byte activa;
}
