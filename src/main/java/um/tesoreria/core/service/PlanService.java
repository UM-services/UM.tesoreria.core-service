/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.PlanException;
import um.tesoreria.core.kotlin.model.Plan;
import um.tesoreria.core.repository.IPlanRepository;

/**
 * @author daniel
 *
 */
@Service
public class PlanService {

	@Autowired
	private IPlanRepository repository;

	public List<Plan> findAll() {
		return repository.findAll();
	}

	public List<Plan> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

	public Plan findByFacultadIdAndPlanId(Integer facultadId, Integer planId) {
		return repository.findByFacultadIdAndPlanId(facultadId, planId)
				.orElseThrow(() -> new PlanException(facultadId, planId));
	}

	public Plan add(Plan plan) {
		repository.save(plan);
		return plan;
	}

	public List<Plan> saveAll(List<Plan> planes) {
		planes = repository.saveAll(planes);
		return planes;
	}

}
