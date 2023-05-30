/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.CursoCargoContratado;

/**
 * @author daniel
 *
 */
@Repository
public interface ICursoCargoContratadoRepository extends JpaRepository<CursoCargoContratado, Long> {

	public List<CursoCargoContratado> findAllByContratadoIdAndAnhoAndMesAndContratoId(Long contratadoId, Integer anho,
			Integer mes, Long contratoId);

	public List<CursoCargoContratado> findAllByCursoIdAndAnhoAndMes(Long cursoId, Integer anho, Integer mes);

	public List<CursoCargoContratado> findAllByCursoIdAndContratoId(Long cursoId, Long contratoId);

	public List<CursoCargoContratado> findAllByAnhoAndMes(Integer anho, Integer mes);

	public Optional<CursoCargoContratado> findByCursoCargoContratadoId(Long cursoCargoContratadoId);

	public Optional<CursoCargoContratado> findByCursoIdAndAnhoAndMesAndContratadoId(Long cursoId, Integer anho,
			Integer mes, Long contratadoId);

	@Modifying
	public void deleteByCursoCargoContratadoId(Long cursoCargoContratadoId);

	@Modifying
	public void deleteAllByCursoIdAndContratoIdAndContratadoId(Long cursoId, Long contratoId, Long contratadoId);

}
