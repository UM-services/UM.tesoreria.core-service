/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.extern.model.view.PreunivMatricResumenFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PreunivMatricResumenFacultadConsumer {

	public List<PreunivMatricResumenFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
			Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preunivmatricresumen/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, PreunivMatricResumenFacultad[].class).getBody());
	}

}
