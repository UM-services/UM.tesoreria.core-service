/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.kotlin.model.Setup;

/**
 * @author daniel
 *
 */
@Repository
public interface ISetupRepository extends JpaRepository<Setup, Long> {

	Optional<Setup> findTopByOrderBySetupIdDesc();

}
