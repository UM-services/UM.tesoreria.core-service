/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Matricula;

/**
 * @author daniel
 *
 */
@Repository
public interface IMatriculaRepository extends JpaRepository<Matricula, Long> {

	public Optional<Matricula> findByMatriculaId(Long matriculaId);

	public Optional<Matricula> findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoId(Integer facultadId,
			BigDecimal personaId, Integer documentoId, Integer lectivoId);

	public Optional<Matricula> findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraIdIn(
			Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId, List<Integer> clases);

	public Optional<Matricula> findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraId(
			Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId, Integer clasechequeraId);

	public List<Matricula> findAllByChequerapendiente(Byte chequerapendiente, Sort sort);

	@Modifying
	public void deleteAllByLectivoIdLessThan(Integer lectivoId);

	@Modifying
	public void deleteAllByProvisoria(Byte provisoria);

	@Modifying
	public void deleteByMatriculaId(Long matriculaId);

}
