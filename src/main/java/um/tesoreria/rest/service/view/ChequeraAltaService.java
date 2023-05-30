/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.ChequeraAlta;
import um.tesoreria.rest.repository.view.IChequeraAltaRepository;
import um.tesoreria.rest.model.view.ChequeraAlta;
import um.tesoreria.rest.repository.view.IChequeraAltaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraAltaService {

	@Autowired
	private IChequeraAltaRepository repository;

	public List<ChequeraAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
                                                                             Integer geograficaId) {
		return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
	}

}
