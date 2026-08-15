package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalesResultado {

    private Boolean result;
    private Persona persona;
    private Domicilio domicilio;

}
