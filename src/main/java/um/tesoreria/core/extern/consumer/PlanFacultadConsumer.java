/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.core.extern.model.kotlin.PlanFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PlanFacultadConsumer {

	public List<PlanFacultad> findAll(String server, Long port) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/plan/";
		return Arrays.asList(restTemplate.getForEntity(url, PlanFacultad[].class).getBody());
	}

	public PlanFacultad findByUnique(String server, Long port, Integer facultadId, Integer planId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/plan/unique/" + facultadId + "/" + planId;
		return restTemplate.getForObject(url, PlanFacultad.class);
	}

}
