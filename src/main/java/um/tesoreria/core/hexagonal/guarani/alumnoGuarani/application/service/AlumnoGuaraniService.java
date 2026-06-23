package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CheckAllPreuniversitarioWithoutChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePreuniversitarioUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoDeteccionRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoGuaraniService {

    private final CreatePreuniversitarioUseCase createPreuniversitarioUseCase;
    private final CheckAllPreuniversitarioWithoutChequeraUseCase checkAllPreuniversitarioWithoutChequeraUseCase;

    public AlumnoGuarani createPreuniversitario(AlumnoGuarani alumnoGuarani) {
        return createPreuniversitarioUseCase.createPreuniversitario(alumnoGuarani);
    }

    public List<AlumnoDeteccionRequest> desmarcarEnviados(List<AlumnoDeteccionRequest> encontrados) {
        return checkAllPreuniversitarioWithoutChequeraUseCase.desmarcarEnviados(encontrados);
    }

}
