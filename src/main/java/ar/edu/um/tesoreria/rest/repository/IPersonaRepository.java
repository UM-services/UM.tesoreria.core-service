/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.um.tesoreria.rest.kotlin.model.Persona;

/**
 * @author daniel
 *
 */
public interface IPersonaRepository extends JpaRepository<Persona, Long> {

	public Optional<Persona> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	public Optional<Persona> findTopByPersonaId(BigDecimal personaId);

	public Optional<Persona> findByUniqueId(Long uniqueId);

	public List<Persona> findAllByCbuLike(String cbu);

}
