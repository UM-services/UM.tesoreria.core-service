/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.CarreraChequera;

/**
 * @author daniel
 *
 */
@Repository
public interface ICarreraChequeraRepository extends JpaRepository<CarreraChequera, Long> {

	public List<CarreraChequera> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer claseChequeraId, Integer curso);

	public Optional<CarreraChequera> findByCarreraChequeraId(Long carreraChequeraId);

	public Optional<CarreraChequera> findByFacultadIdAndLectivoIdAndPlanIdAndCarreraIdAndClaseChequeraIdAndCursoAndGeograficaId(
			Integer facultadId, Integer lectivoId, Integer planId, Integer carreraId, Integer claseChequeraId,
			Integer curso, Integer geograficaId);

}
