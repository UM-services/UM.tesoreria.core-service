/**
 * 
 */
package um.tesoreria.rest.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.IngresoAsientoException;
import um.tesoreria.rest.model.IngresoAsiento;
import um.tesoreria.rest.repository.IIngresoAsientoRepository;
import um.tesoreria.rest.exception.IngresoAsientoException;
import um.tesoreria.rest.repository.IIngresoAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class IngresoAsientoService {

	@Autowired
	private IIngresoAsientoRepository repository;

	public IngresoAsiento findByUnique(OffsetDateTime fechaContable, Integer tipoPagoId) {
		return repository.findByFechaContableAndTipoPagoId(fechaContable, tipoPagoId)
				.orElseThrow(() -> new IngresoAsientoException(fechaContable, tipoPagoId));
	}

}
