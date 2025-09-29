/**
 * 
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import um.tesoreria.core.exception.AsientoException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.Asiento;
import um.tesoreria.core.repository.AsientoRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class AsientoService {

	private final AsientoRepository repository;

	public AsientoService(AsientoRepository repository) {
		this.repository = repository;
	}

	public Asiento findByAsiento(OffsetDateTime fecha, Integer orden) {
		log.debug("Processing AsientoService.findByAsiento");
		return repository.findByFechaAndOrden(fecha, orden).orElseThrow(() -> new AsientoException(fecha, orden));
	}

	@Transactional
	public void deleteByAsientoId(Long asientoId) {
		log.debug("Processing AsientoService.deleteByAsientoId");
		repository.deleteByAsientoId(asientoId);
        log.debug("Asiento deleted");
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
