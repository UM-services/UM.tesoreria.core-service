/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.kotlin.model.Domicilio;

/**
 * @author daniel
 *
 */
public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {
	
	Optional<Domicilio> findByDomicilioId(Long domicilioId);

	Optional<Domicilio> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId);

}
