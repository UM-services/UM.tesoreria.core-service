/**
 * 
 */
package um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import um.tesoreria.rest.model.Legajo;

/**
 * @author daniel
 *
 */
public interface ILegajoRepository extends JpaRepository<Legajo, Long> {

	public Optional<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId,
			Integer documentoId);

}
