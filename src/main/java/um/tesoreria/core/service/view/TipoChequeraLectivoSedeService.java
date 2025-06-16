/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.TipoChequeraLectivoSede;
import um.tesoreria.core.repository.view.TipoChequeraLectivoSedeRepository;

/**
 * @author daniel
 *
 */
@Service
public class TipoChequeraLectivoSedeService {
	
	@Autowired
	private TipoChequeraLectivoSedeRepository repository;

	public List<TipoChequeraLectivoSede> findAllByDisenho(Integer facultadId, Integer lectivoId, Integer geograficaId) {
		if (geograficaId == 0)
			return repository.findAllByFacultadIdAndLectivoId(facultadId, lectivoId);
		return repository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId);
	}
}
