/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.TipoChequeraLectivoSede;
import ar.edu.um.tesoreria.rest.repository.view.ITipoChequeraLectivoSedeRepository;

/**
 * @author daniel
 *
 */
@Service
public class TipoChequeraLectivoSedeService {
	
	@Autowired
	private ITipoChequeraLectivoSedeRepository repository;

	public List<TipoChequeraLectivoSede> findAllByDisenho(Integer facultadId, Integer lectivoId, Integer geograficaId) {
		if (geograficaId == 0)
			return repository.findAllByFacultadIdAndLectivoId(facultadId, lectivoId);
		return repository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId);
	}
}
