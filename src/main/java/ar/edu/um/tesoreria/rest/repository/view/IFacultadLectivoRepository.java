/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.FacultadLectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadLectivoRepository extends JpaRepository<FacultadLectivo, String> {

	public List<FacultadLectivo> findAllByLectivoIdAndFacultadIdIn(Integer lectivoId, List<Integer> facultades);

}
