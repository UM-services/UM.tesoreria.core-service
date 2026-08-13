/**
 *
 */
package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.entity.LegajoEntity;

/**
 * @author daniel
 *
 */
public interface JpaLegajoRepository extends JpaRepository<LegajoEntity, Long> {

	List<LegajoEntity> findAllByFacultadId(Integer facultadId);

	Optional<LegajoEntity> findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId,
																	  Integer documentoId);

}
