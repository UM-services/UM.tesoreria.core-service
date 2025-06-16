/**
 * 
 */
package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ContratoExcluido;

/**
 * @author daniel
 *
 */
@Repository
public interface ContratoExcluidoRepository extends JpaRepository<ContratoExcluido, String> {

}
