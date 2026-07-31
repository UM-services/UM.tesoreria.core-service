/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraCuotaPersona;
import um.tesoreria.core.repository.view.ChequeraCuotaPersonaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraCuotaPersonaService {

	private final ChequeraCuotaPersonaRepository repository;

	public Optional<ChequeraCuotaPersona> findByPersonaIdAndDocumentoIdAndFacultadIdAndAnhoAndMes(BigDecimal personaId,
			Integer documentoId, Integer facultadId, Integer anho, Integer mes) {
		return repository.findTopByPersonaIdAndDocumentoIdAndFacultadIdAndAnhoAndMes(personaId, documentoId, facultadId,
				anho, mes);
	}

}
