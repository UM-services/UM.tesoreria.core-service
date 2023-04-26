/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ICarreraRepository extends JpaRepository<Carrera, Long> {

	public List<Carrera> findAllByFacultadId(Integer facultadId);

	public Optional<Carrera> findByFacultadIdAndPlanIdAndCarreraId(Integer facultadId, Integer planId, Integer carreraId);

}
