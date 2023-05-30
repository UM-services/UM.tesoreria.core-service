/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.Provincia;

/**
 * @author daniel
 *
 */
@Repository
public interface IProvinciaRepository extends JpaRepository<Provincia, Long> {

	public List<Provincia> findAllByFacultadId(Integer facultadId);

	public Optional<Provincia> findByUniqueId(Long uniqueId);

	public Optional<Provincia> findByFacultadIdAndProvinciaId(Integer facultadId, Integer provinciaId);

	public Optional<Provincia> findByFacultadIdAndNombre(Integer facultadId, String nombre);

	public Optional<Provincia> findTopByFacultadId(Integer facultadId);

}
