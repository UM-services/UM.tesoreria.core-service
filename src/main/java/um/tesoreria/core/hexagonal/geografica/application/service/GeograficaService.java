/**
 * 
 */
package um.tesoreria.core.hexagonal.geografica.application.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.GeograficaException;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.domain.ports.in.GetAllGeograficasUseCase;
import um.tesoreria.core.hexagonal.geografica.domain.ports.in.GetGeograficaByIdUseCase;
import um.tesoreria.core.hexagonal.geografica.domain.ports.in.GetGeograficasBySedeUseCase;
import um.tesoreria.core.model.view.GeograficaLectivo;
import um.tesoreria.core.service.view.GeograficaLectivoService;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class GeograficaService {

	private final GeograficaLectivoService geograficaLectivoService;
	private final GetAllGeograficasUseCase getAllGeograficasUseCase;
	private final GetGeograficasBySedeUseCase getGeograficasBySedeUseCase;
	private final GetGeograficaByIdUseCase getGeograficaByIdUseCase;

	public List<Geografica> findAll() {
		return getAllGeograficasUseCase.getAllGeograficas();
	}

	public List<Geografica> findAllBySede(Integer geograficaId) {
		if (geograficaId == 0) {
			return getAllGeograficasUseCase.getAllGeograficas();
		}
		return getGeograficasBySedeUseCase.getGeograficasBySede(geograficaId);
	}

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId) {
		return geograficaLectivoService.findAllByLectivoId(lectivoId);
	}

	public Optional<Geografica> findByGeograficaId(Integer geograficaId) {
		return getGeograficaByIdUseCase.getGeograficaById(geograficaId);
	}

}
