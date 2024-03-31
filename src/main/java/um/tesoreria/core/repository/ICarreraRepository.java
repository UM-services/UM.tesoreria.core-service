/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Carrera;

/**
 * @author daniel
 *
 */
@Repository
public interface ICarreraRepository extends JpaRepository<Carrera, Long> {

	public List<Carrera> findAllByFacultadId(Integer facultadId);

	public Optional<Carrera> findByFacultadIdAndPlanIdAndCarreraId(Integer facultadId, Integer planId, Integer carreraId);

}
