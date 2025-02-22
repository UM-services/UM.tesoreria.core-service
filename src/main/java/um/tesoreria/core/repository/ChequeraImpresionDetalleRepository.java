/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ChequeraImpresionDetalle;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraImpresionDetalleRepository extends JpaRepository<ChequeraImpresionDetalle, Long> {

}
