/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;

import um.tesoreria.core.kotlin.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

	List<Ubicacion> findAllByDependenciaIdNotNull();

}
