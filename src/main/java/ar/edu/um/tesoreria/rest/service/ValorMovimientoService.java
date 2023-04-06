/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.time.OffsetDateTime;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ValorMovimiento;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ValorMovimientoException;
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

	public ValorMovimiento findByValorMovimientoId(Long valorMovimientoId) {
		return repository.findByValorMovimientoId(valorMovimientoId).orElseThrow(() -> new ValorMovimientoException(valorMovimientoId));
	}

	public ValorMovimiento findByNumero(Integer valorId, Long numero) {
		return repository.findByValorIdAndNumero(valorId, numero)
				.orElseThrow(() -> new ValorMovimientoException(valorId, numero));
	}

	public ValorMovimiento findFirstByContable(OffsetDateTime fechaContable, Integer ordenContable) {
		return repository.findFirstByFechaContableAndOrdenContable(fechaContable, ordenContable)
				.orElseThrow(() -> new ValorMovimientoException(fechaContable, ordenContable));
	}

	@Transactional
    public void deleteByValorMovimientoId(Long valorMovimientoId) {
		repository.deleteByValorMovimientoId(valorMovimientoId);
    }
}
