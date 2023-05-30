/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.PlanException;
import um.tesoreria.rest.model.Plan;
import um.tesoreria.rest.repository.IPlanRepository;
import um.tesoreria.rest.exception.PlanException;
import um.tesoreria.rest.repository.IPlanRepository;

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
