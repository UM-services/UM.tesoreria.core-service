/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.CargoMateria;

/**
 * @author daniel
 *
 */
@Repository
public interface ICargoMateriaRepository extends JpaRepository<CargoMateria, Integer> {

}
