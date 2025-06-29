/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.PersonaSuspendido;

/**
 * @author daniel
 *
 */
@Repository
public interface PeriodoSuspendidoRepository extends JpaRepository<PersonaSuspendido, Long> {

	public List<PersonaSuspendido> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId);

	public Optional<PersonaSuspendido> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);

	public Optional<PersonaSuspendido> findByPersonaSuspendidoId(Long personaSuspendidoId);

	@Modifying
	public void deleteByPersonaSuspendidoId(Long personaSuspendidoId);

}
