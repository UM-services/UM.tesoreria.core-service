/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.rest.repository.view.IChequeraSerieAltaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraSerieAltaService {

	@Autowired
	private IChequeraSerieAltaRepository repository;

	public List<ChequeraSerieAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
																				  Integer geograficaId) {
		return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
	}

}
