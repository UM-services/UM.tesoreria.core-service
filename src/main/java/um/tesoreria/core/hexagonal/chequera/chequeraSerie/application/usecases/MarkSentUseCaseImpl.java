package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.MarkSentUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class MarkSentUseCaseImpl implements MarkSentUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    @Transactional
    public ChequeraSerie markSent(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        log.debug("Processing ChequeraSerieService.markSent");
        var chequeraSerie = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        chequeraSerie.setEnviado((byte) 1);
        return repository.save(chequeraSerie);
    }
}
