/**
 * 
 */
package um.tesoreria.rest.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.view.ContratadoPersona;

/**
 * @author daniel
 *
 */
@Repository
public interface IContratadoPersonaRepository extends JpaRepository<ContratadoPersona, Long> {

}
