/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.Setup;

/**
 * @author daniel
 *
 */
@Repository
public interface ISetupRepository extends JpaRepository<Setup, Long> {

	public Optional<Setup> findTopByOrderBySetupIdDesc();

}
