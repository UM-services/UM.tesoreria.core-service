/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraImpresionCabecera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraImpresionCabeceraRepository extends JpaRepository<ChequeraImpresionCabecera, Long> {

}
