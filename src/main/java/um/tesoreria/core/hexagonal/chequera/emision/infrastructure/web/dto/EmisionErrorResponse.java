package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmisionErrorResponse {

    private String codigo;
    private String mensaje;
}
