/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.IngresoAsientoException;
import um.tesoreria.core.model.IngresoAsiento;
import um.tesoreria.core.repository.IngresoAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class IngresoAsientoService {

	private final IngresoAsientoRepository repository;

	public IngresoAsiento findByUnique(OffsetDateTime fechaContable, Integer tipoPagoId) {
		return repository.findByFechaContableAndTipoPagoId(fechaContable, tipoPagoId)
				.orElseThrow(() -> new IngresoAsientoException(fechaContable, tipoPagoId));
	}

}
