/**
 * 
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.IngresoAsientoException;
import um.tesoreria.core.model.IngresoAsiento;
import um.tesoreria.core.repository.IIngresoAsientoRepository;

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
