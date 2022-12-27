/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.model.Provincia;

/**
 * @author daniel
 *
 */
@Service
public class ProvinciaFacultadConsumer {

	public Provincia findByUnique(String server, Long port, Integer facultadId, Integer provinciaId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/provincia/unique/" + facultadId + "/" + provinciaId;
		return restTemplate.getForObject(url, Provincia.class);
	}

}
