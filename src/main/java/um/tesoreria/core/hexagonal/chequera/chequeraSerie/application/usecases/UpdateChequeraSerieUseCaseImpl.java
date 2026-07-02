package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.UpdateChequeraSerieUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateChequeraSerieUseCaseImpl implements UpdateChequeraSerieUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    @Transactional
    public ChequeraSerie update(ChequeraSerie newChequeraSerie, Long chequeraId) {
        log.debug("Processing ChequeraSerieService.update for id {}", chequeraId);
        ChequeraSerie chequeraSerie = repository.findByChequeraId(chequeraId)
                .orElseThrow(() -> new ChequeraSerieException(chequeraId));

        chequeraSerie.setFacultadId(newChequeraSerie.getFacultadId());
        chequeraSerie.setTipoChequeraId(newChequeraSerie.getTipoChequeraId());
        chequeraSerie.setChequeraSerieId(newChequeraSerie.getChequeraSerieId());
        chequeraSerie.setPersonaId(newChequeraSerie.getPersonaId());
        chequeraSerie.setDocumentoId(newChequeraSerie.getDocumentoId());
        chequeraSerie.setLectivoId(newChequeraSerie.getLectivoId());
        chequeraSerie.setArancelTipoId(newChequeraSerie.getArancelTipoId());
        chequeraSerie.setCursoId(newChequeraSerie.getCursoId());
        chequeraSerie.setAsentado(newChequeraSerie.getAsentado());
        chequeraSerie.setGeograficaId(newChequeraSerie.getGeograficaId());
        chequeraSerie.setFecha(newChequeraSerie.getFecha());
        chequeraSerie.setCuotasPagadas(newChequeraSerie.getCuotasPagadas());
        chequeraSerie.setObservaciones(newChequeraSerie.getObservaciones());
        chequeraSerie.setAlternativaId(newChequeraSerie.getAlternativaId());
        chequeraSerie.setAlgoPagado(newChequeraSerie.getAlgoPagado());
        chequeraSerie.setTipoImpresionId(newChequeraSerie.getTipoImpresionId());
        chequeraSerie.setFlagPayperTic(newChequeraSerie.getFlagPayperTic());
        chequeraSerie.setUsuarioId(newChequeraSerie.getUsuarioId());
        chequeraSerie.setEnviado(newChequeraSerie.getEnviado());
        chequeraSerie.setRetenida(newChequeraSerie.getRetenida());
        chequeraSerie.setVersion(newChequeraSerie.getVersion());

        return repository.save(chequeraSerie);
    }
}
