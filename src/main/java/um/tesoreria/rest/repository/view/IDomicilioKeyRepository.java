/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.view.DomicilioKey;

/**
 * @author daniel
 *
 */
@Repository
public interface IDomicilioKeyRepository extends JpaRepository<DomicilioKey, String> {

	public List<DomicilioKey> findAllByUnifiedIn(List<String> keys);

}
