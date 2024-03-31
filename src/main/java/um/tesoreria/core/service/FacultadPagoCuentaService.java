/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.FacultadPagoCuentaException;
import um.tesoreria.core.model.FacultadPagoCuenta;
import um.tesoreria.core.repository.IFacultadPagoCuentaRepository;

/**
 * @author daniel
 *
 */
@Service
public class FacultadPagoCuentaService {

	@Autowired
	private IFacultadPagoCuentaRepository repository;

	public FacultadPagoCuenta findByUnique(Integer facultadId, Integer tipoPagoId) {
		return repository.findByFacultadIdAndTipoPagoId(facultadId, tipoPagoId)
				.orElseThrow(() -> new FacultadPagoCuentaException(facultadId, tipoPagoId));
	}

}
