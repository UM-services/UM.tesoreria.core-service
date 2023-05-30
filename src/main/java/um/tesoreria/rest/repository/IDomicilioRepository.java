/**
 * 
 */
package um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.rest.kotlin.model.Domicilio;

/**
 * @author daniel
 *
 */
public interface IDomicilioRepository extends JpaRepository<Domicilio, Long> {
	
	public Optional<Domicilio> findByDomicilioId(Long domicilioId);

	public Optional<Domicilio> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	public Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId);

}
