/**
 * 
 */
package um.tesoreria.core.extern.consumer.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import um.tesoreria.core.extern.model.view.PreunivCarreraFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PreunivCarreraFacultadConsumer {

	public List<PreunivCarreraFacultad> findAllByCarrera(String server, Long port, Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer turnoId, Integer planId, Integer carreraId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preunivcarrera/carrera/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + turnoId + "/" + planId + "/" + carreraId;
		return Arrays.asList(restTemplate.getForEntity(url, PreunivCarreraFacultad[].class).getBody());
	}

	public List<PreunivCarreraFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
			Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preunivcarrera/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, PreunivCarreraFacultad[].class).getBody());
	}

}
