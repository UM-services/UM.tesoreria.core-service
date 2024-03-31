/**
 * 
 */
package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ContratadoPersona;

/**
 * @author daniel
 *
 */
@Repository
public interface IContratadoPersonaRepository extends JpaRepository<ContratadoPersona, Long> {

}
