/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.TipoPago;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoPagoRepository extends JpaRepository<TipoPago, Integer> {

}
