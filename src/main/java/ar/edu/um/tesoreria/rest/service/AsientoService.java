/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.time.OffsetDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.Asiento;
import ar.edu.um.tesoreria.rest.repository.IAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class AsientoService {

	@Autowired
	private IAsientoRepository repository;

	public Asiento findByAsiento(OffsetDateTime fecha, Integer orden) {
		return repository.findByFechaAndOrden(fecha, orden);
	}

	@Transactional
	public void deleteByAsientoId(Long asientoId) {
		repository.deleteByAsientoId(asientoId);
	}

}
