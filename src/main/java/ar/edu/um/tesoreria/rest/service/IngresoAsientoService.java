/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.IngresoAsientoNotFoundException;
import ar.edu.um.tesoreria.rest.model.IngresoAsiento;
import ar.edu.um.tesoreria.rest.repository.IIngresoAsientoRepository;

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
				.orElseThrow(() -> new IngresoAsientoNotFoundException(fechaContable, tipoPagoId));
	}

}
