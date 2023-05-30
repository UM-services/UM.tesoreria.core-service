/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.view.ArancelTipoLectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface IArancelTipoLectivoRepository extends JpaRepository<ArancelTipoLectivo, String> {

	public List<ArancelTipoLectivo> findAllByLectivoIdIn(List<Integer> lectivos);

}
