/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.FacultadPagoCuentaException;
import um.tesoreria.core.model.FacultadPagoCuenta;
import um.tesoreria.core.repository.FacultadPagoCuentaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class FacultadPagoCuentaService {

	private final FacultadPagoCuentaRepository repository;

	public FacultadPagoCuenta findByUnique(Integer facultadId, Integer tipoPagoId) {
		return repository.findByFacultadIdAndTipoPagoId(facultadId, tipoPagoId)
				.orElseThrow(() -> new FacultadPagoCuentaException(facultadId, tipoPagoId));
	}

}
