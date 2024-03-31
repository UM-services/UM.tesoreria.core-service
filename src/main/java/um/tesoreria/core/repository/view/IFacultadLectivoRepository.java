/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.FacultadLectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadLectivoRepository extends JpaRepository<FacultadLectivo, String> {

	public List<FacultadLectivo> findAllByLectivoIdAndFacultadIdIn(Integer lectivoId, List<Integer> facultades);

}
