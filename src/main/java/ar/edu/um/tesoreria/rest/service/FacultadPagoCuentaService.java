/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.FacultadPagoCuentaException;
import ar.edu.um.tesoreria.rest.model.FacultadPagoCuenta;
import ar.edu.um.tesoreria.rest.repository.IFacultadPagoCuentaRepository;

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
