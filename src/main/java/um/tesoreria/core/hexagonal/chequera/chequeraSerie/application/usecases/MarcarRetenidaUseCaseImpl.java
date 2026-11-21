package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.MarcarRetenidaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

/**
 * Sólo toca {@code retenida}: no pisa beca, {@code hpum} ni {@code enviado}, a diferencia del
 * {@code PUT} completo de {@code UpdateChequeraSerieUseCaseImpl}. Al pasar por JPA, el cambio
 * queda auditado como cualquier otra escritura de negocio.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MarcarRetenidaUseCaseImpl implements MarcarRetenidaUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    @Transactional
    public ChequeraSerie marcar(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, boolean retenida) {
        log.debug("Processing MarcarRetenidaUseCaseImpl.marcar {}/{}/{} -> {}",
                facultadId, tipoChequeraId, chequeraSerieId, retenida);
        ChequeraSerie serie = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        serie.setRetenida((byte) (retenida ? 1 : 0));
        return repository.save(serie);
    }
}
