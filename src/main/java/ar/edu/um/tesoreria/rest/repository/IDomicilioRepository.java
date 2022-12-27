/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.um.tesoreria.rest.model.Domicilio;

/**
 * @author daniel
 *
 */
public interface IDomicilioRepository extends JpaRepository<Domicilio, Long> {
	
	public Optional<Domicilio> findByDomicilioId(Long domicilioId);

	public Optional<Domicilio> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	public Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId);

}
