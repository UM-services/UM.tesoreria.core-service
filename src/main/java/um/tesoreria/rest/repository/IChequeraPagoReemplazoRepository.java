/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.ChequeraPagoReemplazo;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraPagoReemplazoRepository extends JpaRepository<ChequeraPagoReemplazo, Long> {

}
