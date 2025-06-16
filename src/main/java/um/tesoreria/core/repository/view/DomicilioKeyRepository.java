/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.DomicilioKey;

/**
 * @author daniel
 *
 */
@Repository
public interface DomicilioKeyRepository extends JpaRepository<DomicilioKey, String> {

	public List<DomicilioKey> findAllByUnifiedIn(List<String> keys);

}
