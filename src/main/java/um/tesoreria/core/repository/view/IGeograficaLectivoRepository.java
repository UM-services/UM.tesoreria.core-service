/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.GeograficaLectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface IGeograficaLectivoRepository extends JpaRepository<GeograficaLectivo, String> {

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId);

}
