package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PersonalesResponse;

public interface CreatePreuniversitarioUseCase {

    AlumnoGuarani createPreuniversitario(PersonalesResponse alumnoGuaraniFull);

}
