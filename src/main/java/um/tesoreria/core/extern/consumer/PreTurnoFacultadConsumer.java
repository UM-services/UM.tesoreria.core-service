/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.core.extern.model.kotlin.PreTurnoFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PreTurnoFacultadConsumer {

	public List<PreTurnoFacultad> findAllByLectivo(String server, Long port, Integer facultadId, Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preturno/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, PreTurnoFacultad[].class).getBody());
	}

}
