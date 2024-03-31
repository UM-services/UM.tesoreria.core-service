/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import um.tesoreria.core.kotlin.model.Build;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IBuildRepository extends JpaRepository<Build, Long> {

	public Optional<Build> findTopByOrderByBuildDesc();

}
