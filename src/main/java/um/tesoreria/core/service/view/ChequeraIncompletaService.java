/**
 *
 */
package um.tesoreria.core.service.view;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.repository.view.ChequeraIncompletaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraIncompletaService {

    private final ChequeraIncompletaRepository repository;

    public List<ChequeraIncompleta> findAllByLectivoIdAndFacultadIdAndGeograficaIdAndClaseChequeraId(Integer lectivoId,
                                                                                                     Integer facultadId,
                                                                                                     Integer geograficaId,
                                                                                                     Integer claseChequeraId) {
        return repository.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraClaseChequeraId(
                lectivoId,
                facultadId,
                geograficaId,
                claseChequeraId,
                Sort.by("persona.apellido").ascending().and(Sort.by("persona.nombre").ascending()));
    }

}
