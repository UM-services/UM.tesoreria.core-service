/**
 * 
 */
package um.tesoreria.rest.extern.consumer.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import um.tesoreria.rest.extern.model.view.PreunivResumenFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PreunivResumenFacultadConsumer {

	public List<PreunivResumenFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
			Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/preunivresumen/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, PreunivResumenFacultad[].class).getBody());
	}

}
