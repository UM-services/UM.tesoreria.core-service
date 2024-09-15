/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Plan;

/**
 * @author daniel
 *
 */
@Repository
public interface IPlanRepository extends JpaRepository<Plan, Long> {

	List<Plan> findAllByFacultadId(Integer facultadId);

	Optional<Plan> findByFacultadIdAndPlanId(Integer facultadId, Integer planId);

}
