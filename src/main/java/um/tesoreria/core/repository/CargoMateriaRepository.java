/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.CargoMateria;

/**
 * @author daniel
 *
 */
@Repository
public interface CargoMateriaRepository extends JpaRepository<CargoMateria, Integer> {

}
