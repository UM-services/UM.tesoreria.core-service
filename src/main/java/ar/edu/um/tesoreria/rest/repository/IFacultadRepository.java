/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Facultad;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadRepository extends JpaRepository<Facultad, Integer> {

	public Optional<Facultad> findByFacultadId(Integer facultadId);

}
