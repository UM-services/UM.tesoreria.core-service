/**
 * 
 */
package um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;

/**
 * @author daniel
 *
 */
public interface JpaDomicilioRepository extends JpaRepository<DomicilioEntity, Long> {
	
	Optional<DomicilioEntity> findByDomicilioId(Long domicilioId);

	Optional<DomicilioEntity> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	Optional<DomicilioEntity> findFirstByPersonaId(BigDecimal personaId);

}
