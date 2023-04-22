/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoPagoRepository extends JpaRepository<TipoPago, Integer> {

}
