/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Materia;

/**
 * @author daniel
 *
 */
@Repository
public interface IMateriaRepository extends JpaRepository<Materia, Long> {

	public List<Materia> findAllByFacultadId(Integer facultadId);

	public Optional<Materia> findByFacultadIdAndPlanIdAndMateriaId(Integer facultadId, Integer planId,
			String materiaId);

}
