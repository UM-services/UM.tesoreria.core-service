package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import org.springframework.data.domain.Sort;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.math.BigDecimal;
import java.util.List;

public interface GetChequeraSerieByPersonaUseCase {
    List<ChequeraSerie> getAllByPersona(BigDecimal personaId, Integer documentoId);
    List<ChequeraSerie> getAllByPersonaExtended(BigDecimal personaId, Integer documentoId);
    List<ChequeraSerie> getAllByPersonaLectivo(BigDecimal personaId, Integer documentoId, Integer lectivoId);
    List<ChequeraSerie> getAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId);
    List<ChequeraSerie> getAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort);
}
