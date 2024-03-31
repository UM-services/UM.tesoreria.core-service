/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.CarreraKey;

/**
 * @author daniel
 *
 */
@Repository
public interface ICarreraKeyRepository extends JpaRepository<CarreraKey, String> {

	public List<CarreraKey> findAllByFacultadId(Integer facultadId);

}
