/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadRepository extends JpaRepository<Facultad, Integer> {

	public List<Facultad> findAllByFacultadIdIn(List<Integer> facultadIds);

	public Optional<Facultad> findByFacultadId(Integer facultadId);

}
