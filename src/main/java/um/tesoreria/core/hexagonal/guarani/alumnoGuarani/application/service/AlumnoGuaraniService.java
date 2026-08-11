package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonalesResultado;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePreuniversitarioUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePersonalesUseCase;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlumnoGuaraniService {

    private final CreatePreuniversitarioUseCase createPreuniversitarioUseCase;
    private final CreatePersonalesUseCase createPersonalesUseCase;

    public AlumnoGuarani createPreuniversitario(AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing AlumnoGuaraniService.createPreuniversitario\n\n");
        return createPreuniversitarioUseCase.createPreuniversitario(alumnoGuarani);
    }

    public PersonalesResultado createPersonales(AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing AlumnoGuaraniService.createPersonales\n\n");
        return createPersonalesUseCase.createPersonales(alumnoGuarani);
    }

}
