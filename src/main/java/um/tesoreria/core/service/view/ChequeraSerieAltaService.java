/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.repository.view.ChequeraSerieAltaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraSerieAltaService {

	@Autowired
	private ChequeraSerieAltaRepository repository;

	public List<ChequeraSerieAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
																				  Integer geograficaId) {
		return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
	}

}
