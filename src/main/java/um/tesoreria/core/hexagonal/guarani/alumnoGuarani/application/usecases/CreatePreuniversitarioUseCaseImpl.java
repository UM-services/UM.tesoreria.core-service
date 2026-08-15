package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.CreatePreuniversitarioChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePreuniversitarioUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PersonalesResponse;
import um.tesoreria.core.service.facade.MailChequeraService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreatePreuniversitarioUseCaseImpl implements CreatePreuniversitarioUseCase {

    private final CreatePreuniversitarioChequeraUseCase createPreuniversitarioChequeraUseCase;
    private final MailChequeraService mailChequeraService;

    @Override
    public AlumnoGuarani createPreuniversitario(PersonalesResponse alumnoGuaraniFull) {
        log.debug("\n\nProcessing CreatePreuniversitarioUseCaseImpl.createPreuniversitario\n\n");
        log.debug("\n\nGeneración de chequera\n\n");
        var chequeraSerie = createPreuniversitarioChequeraUseCase.create(new PreuniversitarioChequeraData(
                alumnoGuaraniFull.getAlumnoGuarani().getPropuesta(),
                alumnoGuaraniFull.getPropuestaGuarani().getResponsablesAcademicas().getFirst()
                        .getResponsableAcademica(),
                alumnoGuaraniFull.getAlumnoGuarani().getUbicacion(),
                alumnoGuaraniFull.getPersona().getPersonaId(),
                alumnoGuaraniFull.getPersona().getDocumentoId()));
        if (chequeraSerie == null) {
            log.info("\n\nChequera serie nula\n\n");
            return alumnoGuaraniFull.getAlumnoGuarani();
        }
        // si la chequera ya existía no la envío
        if (!chequeraSerie.getJustCreated()) {
            return alumnoGuaraniFull.getAlumnoGuarani();
        }

        log.debug("\n\nEnvío de chequera\n\n");
        var result = mailChequeraService.sendChequera(chequeraSerie.getFacultadId(),
                chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getChequeraSerieId(),
                chequeraSerie.getAlternativaId(),
                false,
                false,
                false
        );
        log.info("\n\nResult -> {}", result);
        return alumnoGuaraniFull.getAlumnoGuarani();
    }

}
