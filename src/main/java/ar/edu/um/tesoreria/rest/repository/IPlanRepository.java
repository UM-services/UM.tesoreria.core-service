/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Plan;

/**
 * @author daniel
 *
 */
@Repository
public interface IPlanRepository extends JpaRepository<Plan, Long> {

	public List<Plan> findAllByFacultadId(Integer facultadId);

	public Optional<Plan> findByFacultadIdAndPlanId(Integer facultadId, Integer planId);

}
