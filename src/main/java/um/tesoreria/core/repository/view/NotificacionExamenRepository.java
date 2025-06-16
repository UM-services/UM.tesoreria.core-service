/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.NotificacionExamen;

/**
 * @author daniel
 *
 */
@Repository
public interface NotificacionExamenRepository extends JpaRepository<NotificacionExamen, Long> {

	public List<NotificacionExamen> findAllByFacultadId(Integer facultadId);

}
