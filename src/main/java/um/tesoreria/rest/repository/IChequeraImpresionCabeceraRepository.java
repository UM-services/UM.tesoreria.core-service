/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.ChequeraImpresionCabecera;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraImpresionCabeceraRepository extends JpaRepository<ChequeraImpresionCabecera, Long> {

}
