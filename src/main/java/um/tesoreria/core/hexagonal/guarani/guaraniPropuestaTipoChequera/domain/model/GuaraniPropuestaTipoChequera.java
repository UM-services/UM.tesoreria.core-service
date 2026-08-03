package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuaraniPropuestaTipoChequera {
    private Integer guaraniPropuestaTipoChequeraId;
    private Integer propuestaGuarani;
    private Integer lectivoId;
    private Integer tipoChequeraId;
}
