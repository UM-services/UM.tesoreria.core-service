/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.LegajoKey;

/**
 * @author daniel
 *
 */
@Repository
public interface LegajoKeyRepository extends JpaRepository<LegajoKey, String> {

	public List<LegajoKey> findAllByUnifiedIn(List<String> keys);

	public List<LegajoKey> findAllByFacultadIdAndUnifiedIn(Integer facultadId, List<String> keys);

}
