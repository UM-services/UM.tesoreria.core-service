/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.ChequeraIncompleta;
import ar.edu.um.tesoreria.rest.repository.view.IChequeraIncompletaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraIncompletaService {

	@Autowired
	private IChequeraIncompletaRepository repository;

	public List<ChequeraIncompleta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId,
			Integer facultadId, Integer geograficaId) {
		return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId,
				Sort.by("persona.apellido").ascending().and(Sort.by("persona.nombre").ascending()));
	}

}
