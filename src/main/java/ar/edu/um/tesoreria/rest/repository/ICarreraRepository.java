/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Carrera;

/**
 * @author daniel
 *
 */
@Repository
public interface ICarreraRepository extends JpaRepository<Carrera, Long> {

	public List<Carrera> findAllByFacultadId(Integer facultadId);

	public Optional<Carrera> findByFacultadIdAndPlanIdAndCarreraId(Integer facultadId, Integer planId, Integer carreraId);

}
