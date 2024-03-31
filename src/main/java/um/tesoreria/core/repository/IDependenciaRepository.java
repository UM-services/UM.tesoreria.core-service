/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Dependencia;

/**
 * @author daniel
 *
 */
@Repository
public interface IDependenciaRepository extends JpaRepository<Dependencia, Integer> {

}
