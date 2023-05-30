/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.ChequeraImpresionDetalle;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraImpresionDetalleRepository extends JpaRepository<ChequeraImpresionDetalle, Long> {

}
