/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ChequeraPagoReemplazo;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraPagoReemplazoRepository extends JpaRepository<ChequeraPagoReemplazo, Long> {

}
