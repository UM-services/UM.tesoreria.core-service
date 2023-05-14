/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.CarreraChequera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
