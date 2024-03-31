/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.UbicacionArticulo;

/**
 * @author daniel
 *
 */
@Repository
public interface IUbicacionArticuloRepository extends JpaRepository<UbicacionArticulo, Long> {

}
