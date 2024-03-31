/**
 * 
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;

import um.tesoreria.core.exception.AsientoException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.Asiento;
import um.tesoreria.core.repository.IAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class AsientoService {

	@Autowired
	private IAsientoRepository repository;

	public Asiento findByAsiento(OffsetDateTime fecha, Integer orden) {
		return repository.findByFechaAndOrden(fecha, orden).orElseThrow(() -> new AsientoException(fecha, orden));
	}

	@Transactional
	public void deleteByAsientoId(Long asientoId) {
		repository.deleteByAsientoId(asientoId);
	}

	@Transactional
	public Asiento add(Asiento asiento) {
		return repository.save(asiento);
	}

	public Asiento update(Asiento newAsiento, Long asientoId) {
		return repository.findByAsientoId(asientoId).map(asiento -> {
			asiento = new Asiento(asientoId, newAsiento.getFecha(), newAsiento.getOrden(), newAsiento.getVinculo(), newAsiento.getFechaContra(), newAsiento.getOrdenContra(), newAsiento.getTrackId(), null);
			asiento = repository.save(asiento);
			return asiento;
		}).orElseThrow(() -> new AsientoException(asientoId));
	}
}
