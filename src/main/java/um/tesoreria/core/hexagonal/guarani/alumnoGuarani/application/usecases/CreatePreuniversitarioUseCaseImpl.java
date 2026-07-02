package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.PreuniversitarioChequeraService;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePreuniversitarioUseCase;
import um.tesoreria.core.service.facade.MailChequeraService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreatePreuniversitarioUseCaseImpl implements CreatePreuniversitarioUseCase {

    private final PreuniversitarioChequeraService preuniversitarioChequeraService;
    private final MailChequeraService mailChequeraService;

    @Override
    public AlumnoGuarani createPreuniversitario(AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing CreatePreuniversitarioUseCaseImpl.createPreuniversitario\n\n");
        log.debug("\n\nGeneración de chequera\n\n");
        var chequeraSerie = preuniversitarioChequeraService.create(alumnoGuarani);
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
        return alumnoGuarani;
    }

}
