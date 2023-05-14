/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.CargoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ICargoMateriaRepository extends JpaRepository<CargoMateria, Integer> {

}
