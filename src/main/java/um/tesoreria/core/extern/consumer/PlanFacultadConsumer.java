/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.PlanFacultad;

@Service
public class PlanFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<PlanFacultad> findAll(String server, Long port) {
		String url = "http://" + server + ":" + port + "/plan/";
		return Arrays.asList(restClient.get().uri(url).retrieve().toEntity(PlanFacultad[].class).getBody());
	}

	public PlanFacultad findByUnique(String server, Long port, Integer facultadId, Integer planId) {
		String url = "http://" + server + ":" + port + "/plan/unique/" + facultadId + "/" + planId;
		return restClient.get().uri(url).retrieve().body(PlanFacultad.class);
	}

}
