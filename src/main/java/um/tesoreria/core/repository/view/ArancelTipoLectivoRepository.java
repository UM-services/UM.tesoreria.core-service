/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ArancelTipoLectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface ArancelTipoLectivoRepository extends JpaRepository<ArancelTipoLectivo, String> {

	public List<ArancelTipoLectivo> findAllByLectivoIdIn(List<Integer> lectivos);

}
