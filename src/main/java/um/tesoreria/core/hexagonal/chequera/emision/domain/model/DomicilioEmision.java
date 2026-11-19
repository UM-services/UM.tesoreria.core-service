package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioEmision {

    private Long domicilioId;
    private String emailPersonal;
    private String emailInstitucional;
    private String emailPagador;
    private boolean emailPersonalValido;
    private boolean emailInstitucionalValido;
}
