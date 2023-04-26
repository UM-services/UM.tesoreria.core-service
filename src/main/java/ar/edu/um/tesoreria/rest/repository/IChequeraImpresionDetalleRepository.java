/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraImpresionDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraImpresionDetalleRepository extends JpaRepository<ChequeraImpresionDetalle, Long> {

}
