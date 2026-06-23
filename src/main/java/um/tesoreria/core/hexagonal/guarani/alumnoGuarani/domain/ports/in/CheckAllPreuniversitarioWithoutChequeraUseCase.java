package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoDeteccionRequest;

import java.util.List;

public interface CheckAllPreuniversitarioWithoutChequeraUseCase {

    List<AlumnoDeteccionRequest> desmarcarEnviados(List<AlumnoDeteccionRequest> encontrados);

}
