/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorValor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ProveedorValorException;
import ar.edu.um.tesoreria.rest.repository.IProveedorValorRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorValorService {

	@Autowired
	private IProveedorValorRepository repository;

	public List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
		return repository.findAllByProveedorMovimientoId(proveedorMovimientoId);
	}

	public ProveedorValor findByValorMovimientoId(Long valorMovimientoId) {
		return repository.findByValorMovimientoId(valorMovimientoId)
				.orElseThrow(() -> new ProveedorValorException(valorMovimientoId));
	}

	public ProveedorValor findByProveedorvalorId(Long proveedorValorId) {
		return repository.findByProveedorValorId(proveedorValorId)
				.orElseThrow(() -> new ProveedorValorException());
	}

	@Transactional
    public void deleteByProveedorValorId(Long proveedorValorId) {
		repository.deleteByProveedorValorId(proveedorValorId);
    }

}
