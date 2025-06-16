/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.repository.view.ChequeraIncompletaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraIncompletaService {

	@Autowired
	private ChequeraIncompletaRepository repository;

	public List<ChequeraIncompleta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId,
                                                                                   Integer facultadId, Integer geograficaId) {
		return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId,
				Sort.by("persona.apellido").ascending().and(Sort.by("persona.nombre").ascending()));
	}

}
