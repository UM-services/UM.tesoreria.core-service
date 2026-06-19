package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto;

import lombok.Getter;
import lombok.Setter;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
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
    private BigDecimal importe;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
