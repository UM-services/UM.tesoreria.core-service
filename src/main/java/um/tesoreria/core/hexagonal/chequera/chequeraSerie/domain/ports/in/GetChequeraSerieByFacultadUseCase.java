package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.math.BigDecimal;
import java.util.List;

public interface GetChequeraSerieByFacultadUseCase {
    List<ChequeraSerie> getAllByFacultad(BigDecimal personaId, Integer documentoId, Integer facultadId);
    List<ChequeraSerie> getAllByFacultadExtended(BigDecimal personaId, Integer documentoId, Integer facultadId);
    List<ChequeraSerie> getAllByLectivoAndFacultad(Integer lectivoId, Integer facultadId);
    List<ChequeraSerie> getAllByLectivoAndFacultadAndGeografica(Integer lectivoId, Integer facultadId, Integer geograficaId);
    List<ChequeraSerie> getAllBySede(Integer facultadId, Integer lectivoId, Integer geograficaId);
    List<ChequeraSerie> getAllByGeograficaAndFacultadsAndLectivos(Integer geograficaId, List<Integer> facultadIds, List<Integer> lectivoIds);
    List<ChequeraSerie> getAllByFacultadAndLectivoAndTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId);
    List<ChequeraSerie> getAllByDocumentos(Integer facultadId, Integer lectivoId, Integer geograficaId, List<BigDecimal> personaIds);
}
