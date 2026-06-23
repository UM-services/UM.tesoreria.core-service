package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;

public interface CreatePreuniversitarioUseCase {

    AlumnoGuarani createPreuniversitario(AlumnoGuarani alumnoGuarani);

}
