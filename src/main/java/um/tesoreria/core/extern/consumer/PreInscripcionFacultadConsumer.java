/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.core.extern.model.kotlin.PreInscripcionFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PreInscripcionFacultadConsumer {

	public List<PreInscripcionFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
														 Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preinscripcion/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, PreInscripcionFacultad[].class).getBody());
	}

	public List<PreInscripcionFacultad> findAllByPreInscriptos(String server, Long port, Integer facultadId,
			Integer lectivoId, Integer geograficaId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preinscripcion/sede/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId;
		return Arrays.asList(restTemplate.getForEntity(url, PreInscripcionFacultad[].class).getBody());
	}

}
