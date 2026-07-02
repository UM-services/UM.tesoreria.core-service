/**
 * 
 */
package um.tesoreria.core.hexagonal.contable.asiento.application.service;

import java.time.OffsetDateTime;

import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository.JpaAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class AsientoService {

	private final JpaAsientoRepository repository;

	public AsientoService(JpaAsientoRepository repository) {
		this.repository = repository;
	}

	public AsientoEntity findByAsiento(OffsetDateTime fecha, Integer orden) {
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
	public AsientoEntity add(AsientoEntity asiento) {
		return repository.save(asiento);
	}

	public AsientoEntity update(AsientoEntity newAsiento, Long asientoId) {
		return repository.findByAsientoId(asientoId).map(asiento -> {
			asiento = new AsientoEntity(asientoId, newAsiento.getFecha(), newAsiento.getOrden(), newAsiento.getVinculo(), newAsiento.getFechaContra(), newAsiento.getOrdenContra(), newAsiento.getTrackId(), null);
			asiento = repository.save(asiento);
			return asiento;
		}).orElseThrow(() -> new AsientoException(asientoId));
	}

}
