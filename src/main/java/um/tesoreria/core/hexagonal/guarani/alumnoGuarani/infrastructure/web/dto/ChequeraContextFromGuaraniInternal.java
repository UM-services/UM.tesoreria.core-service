package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.*;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraContextFromGuaraniInternal {

    private Integer facultadId;
    private Integer geograficaId;
    private Integer tipoChequeraId;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
