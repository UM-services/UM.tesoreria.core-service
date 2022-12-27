/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.LegajoKey;

/**
 * @author daniel
 *
 */
@Repository
public interface ILegajoKeyRepository extends JpaRepository<LegajoKey, String> {

	public List<LegajoKey> findAllByUnifiedIn(List<String> keys);

	public List<LegajoKey> findAllByFacultadIdAndUnifiedIn(Integer facultadId, List<String> keys);

}
