/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ValorMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.model.ValorMovimiento;
import ar.edu.um.tesoreria.rest.repository.IValorMovimientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ValorMovimientoService {

	@Autowired
	private IValorMovimientoRepository repository;

	public List<ValorMovimiento> findAllByValorMovimientoIdIn(List<Long> valorMovimientoIds) {
		return repository.findAllByValorMovimientoIdIn(valorMovimientoIds);
	}

	public ValorMovimiento findByNumero(Integer valorId, Long numero) {
		return repository.findByValorIdAndNumero(valorId, numero)
				.orElseThrow(() -> new ValorMovimientoNotFoundException(valorId, numero));
	}

	public ValorMovimiento findFirstByContable(OffsetDateTime fechaContable, Integer ordenContable) {
		return repository.findFirstByFechaContableAndOrdenContable(fechaContable, ordenContable)
				.orElseThrow(() -> new ValorMovimientoNotFoundException(fechaContable, ordenContable));
	}

}
