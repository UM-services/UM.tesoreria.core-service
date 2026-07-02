package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByFacultadUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieByFacultadUseCaseImpl implements GetChequeraSerieByFacultadUseCase {

    private final ChequeraSerieRepository repository;
    private final CalculateDeudaUseCase calculateDeudaUseCase;

    @Override
    public List<ChequeraSerie> getAllByFacultad(BigDecimal personaId, Integer documentoId, Integer facultadId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId);
    }

    @Override
    public List<ChequeraSerie> getAllByFacultadExtended(BigDecimal personaId, Integer documentoId, Integer facultadId) {
        List<ChequeraSerie> chequeras = repository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId);
        for (ChequeraSerie chequera : chequeras) {
            setDeuda(chequera);
        }
        return chequeras;
    }

    @Override
    public List<ChequeraSerie> getAllByLectivoAndFacultad(Integer lectivoId, Integer facultadId) {
        return repository.findAllByLectivoIdAndFacultadId(lectivoId, facultadId);
    }

    @Override
    public List<ChequeraSerie> getAllByLectivoAndFacultadAndGeografica(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
    }

    @Override
    public List<ChequeraSerie> getAllBySede(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId);
    }

    @Override
    public List<ChequeraSerie> getAllByGeograficaAndFacultadsAndLectivos(Integer geograficaId, List<Integer> facultadIds, List<Integer> lectivoIds) {
        return repository.findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(geograficaId, facultadIds, lectivoIds);
    }

    @Override
    public List<ChequeraSerie> getAllByFacultadAndLectivoAndTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId);
    }

    @Override
    public List<ChequeraSerie> getAllByDocumentos(Integer facultadId, Integer lectivoId, Integer geograficaId, List<BigDecimal> personaIds) {
        return repository.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(facultadId, lectivoId, geograficaId, personaIds);
    }

    private void setDeuda(ChequeraSerie chequera) {
        var deuda = calculateDeudaUseCase.calculateDeuda(chequera);
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
    }
}
