/**
 * 
 */
package um.tesoreria.core.service.view;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraCuotaPersona;
import um.tesoreria.core.repository.view.IChequeraCuotaPersonaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraCuotaPersonaService {

	@Autowired
	private IChequeraCuotaPersonaRepository repository;

	public Optional<ChequeraCuotaPersona> findByPersonaIdAndDocumentoIdAndFacultadIdAndAnhoAndMes(BigDecimal personaId,
			Integer documentoId, Integer facultadId, Integer anho, Integer mes) {
		return repository.findTopByPersonaIdAndDocumentoIdAndFacultadIdAndAnhoAndMes(personaId, documentoId, facultadId,
				anho, mes);
	}

}
