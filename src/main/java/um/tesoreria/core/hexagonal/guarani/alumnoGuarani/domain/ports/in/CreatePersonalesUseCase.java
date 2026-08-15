package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonalesResultado;

public interface CreatePersonalesUseCase {

    PersonalesResultado createPersonales(AlumnoGuarani alumnoGuarani);

}
