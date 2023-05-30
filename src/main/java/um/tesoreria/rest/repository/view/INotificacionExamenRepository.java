/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.NotificacionExamen;

/**
 * @author daniel
 *
 */
@Repository
public interface INotificacionExamenRepository extends JpaRepository<NotificacionExamen, Long> {

	public List<NotificacionExamen> findAllByFacultadId(Integer facultadId);

}
