package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.exception.TipoChequeraException;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.*;
import um.tesoreria.core.model.view.TipoChequeraSearch;
import um.tesoreria.core.service.view.TipoChequeraSearchService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TipoChequeraService {

    private final CreateTipoChequeraUseCase createTipoChequeraUseCase;
    private final GetAllTipoChequeraUseCase getAllTipoChequeraUseCase;
    private final GetAllTipoChequeraAsignableUseCase getAllTipoChequeraAsignableUseCase;
    private final GetAllTipoChequeraByFacultadAndGeograficaUseCase getAllTipoChequeraByFacultadAndGeograficaUseCase;
    private final GetAllTipoChequeraByClaseChequeraUseCase getAllTipoChequeraByClaseChequeraUseCase;
    private final GetTipoChequeraByIdUseCase getTipoChequeraByIdUseCase;
    private final GetLastTipoChequeraUseCase getLastTipoChequeraUseCase;
    private final UpdateTipoChequeraUseCase updateTipoChequeraUseCase;
    private final DeleteTipoChequeraUseCase deleteTipoChequeraUseCase;
    private final MarkTipoChequeraUseCase markTipoChequeraUseCase;
    private final UnmarkTipoChequeraUseCase unmarkTipoChequeraUseCase;
    private final TipoChequeraSearchService tipoChequeraSearchService;

    public List<TipoChequeraSearch> findAllByStrings(List<String> conditions) {
        log.debug("Processing findAllByStrings");
        return tipoChequeraSearchService.findAllByStrings(conditions);
    }

    public List<TipoChequera> findAll() {
        return getAllTipoChequeraUseCase.getAllTipoChequera();
    }

    public List<TipoChequera> findAllAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                               Integer claseChequeraId) {
        return getAllTipoChequeraAsignableUseCase.getAllTipoChequeraAsignable(facultadId, lectivoId, geograficaId, claseChequeraId);
    }

    public List<TipoChequera> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId) {
        return getAllTipoChequeraByFacultadAndGeograficaUseCase.getAllTipoChequeraByFacultadAndGeografica(facultadId, geograficaId);
    }

    public List<TipoChequera> findAllByClaseChequera(Integer claseChequeraId) {
        return getAllTipoChequeraByClaseChequeraUseCase.getAllTipoChequeraByClaseChequera(claseChequeraId);
    }

    public List<TipoChequera> findAllByClaseChequeraIds(List<Integer> claseChequeraIds) {
        return getAllTipoChequeraByClaseChequeraUseCase.getAllTipoChequeraByClaseChequeraIds(claseChequeraIds);
    }

    public TipoChequera findByTipoChequeraId(Integer tipoChequeraId) {
        return getTipoChequeraByIdUseCase.getTipoChequeraById(tipoChequeraId)
                .orElseThrow(() -> new TipoChequeraException(tipoChequeraId));
    }

    public TipoChequera findLast() {
        return getLastTipoChequeraUseCase.getLastTipoChequera()
                .orElseThrow(TipoChequeraException::new);
    }

    public TipoChequera add(TipoChequera tipoChequera) {
        return createTipoChequeraUseCase.createTipoChequera(tipoChequera);
    }

    public TipoChequera update(TipoChequera newTipoChequera, Integer tipoChequeraId) {
        return updateTipoChequeraUseCase.updateTipoChequera(newTipoChequera, tipoChequeraId);
    }

    public void delete(Integer tipoChequeraId) {
        deleteTipoChequeraUseCase.deleteTipoChequera(tipoChequeraId);
    }

    public void unmark() {
        unmarkTipoChequeraUseCase.unmarkTipoChequera();
    }

    public TipoChequera mark(Integer tipochequeraId, Byte imprimir) {
        return markTipoChequeraUseCase.markTipoChequera(tipochequeraId, imprimir);
    }

}
