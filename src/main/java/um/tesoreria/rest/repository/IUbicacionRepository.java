/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.List;

import um.tesoreria.rest.kotlin.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IUbicacionRepository extends JpaRepository<Ubicacion, Integer> {

	public List<Ubicacion> findAllByDependenciaIdNotNull();

}
