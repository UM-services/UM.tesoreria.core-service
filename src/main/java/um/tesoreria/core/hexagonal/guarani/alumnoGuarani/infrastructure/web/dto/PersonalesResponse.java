package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaGuarani;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.web.dto.DomicilioResponse;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.PersonaResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalesResponse {

    private Boolean result;
    private AlumnoGuarani alumnoGuarani;
    private PropuestaGuarani propuestaGuarani;
    private PersonaResponse persona;
    private DomicilioResponse domicilio;

}
