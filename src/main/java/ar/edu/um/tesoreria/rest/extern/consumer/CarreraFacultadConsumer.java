/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import java.util.Arrays;
import java.util.List;

import ar.edu.um.tesoreria.rest.extern.model.kotlin.CarreraFacultad;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author daniel
 *
 */
@Service
public class CarreraFacultadConsumer {

	public List<CarreraFacultad> findAll(String server, Long port) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/carrera/";
		return Arrays.asList(restTemplate.getForEntity(url, CarreraFacultad[].class).getBody());
	}

	public CarreraFacultad findByUnique(String server, Long port, Integer facultadId, Integer planId,
			Integer carreraId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/carrera/unique/" + facultadId + "/" + planId + "/" + carreraId;
		return restTemplate.getForObject(url, CarreraFacultad.class);
	}

}
