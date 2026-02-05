/**
 * 
 */
package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;

/**
 * @author daniel
 *
 */
public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {

	Optional<PersonaEntity> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	Optional<PersonaEntity> findTopByPersonaId(BigDecimal personaId);

	Optional<PersonaEntity> findByUniqueId(Long uniqueId);

	List<PersonaEntity> findAllByCbuLike(String cbu);

}
