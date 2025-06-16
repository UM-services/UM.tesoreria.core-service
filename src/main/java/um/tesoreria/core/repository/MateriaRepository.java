/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.Materia;

/**
 * @author daniel
 *
 */
@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

	public List<Materia> findAllByFacultadId(Integer facultadId);

	public Optional<Materia> findByFacultadIdAndPlanIdAndMateriaId(Integer facultadId, Integer planId,
			String materiaId);

}
