/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.ChequeraCuotaPersona;
import ar.edu.um.tesoreria.rest.repository.view.IChequeraCuotaPersonaRepository;

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
