/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.TipoPago;

/**
 * @author daniel
 *
 */
@Repository
public interface TipoPagoRepository extends JpaRepository<TipoPago, Integer> {

}
