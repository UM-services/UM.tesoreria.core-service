/**
 * 
 */
package um.tesoreria.rest.extern.consumer.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import um.tesoreria.rest.extern.model.view.PersonaKeyFacultad;

/**
 * @author daniel
 *
 */
@Service
public class PersonaKeyFacultadConsumer {

	public List<PersonaKeyFacultad> findAllByUnifieds(String server, Long port, List<String> keys) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "http://" + server + ":" + port + "/personakey/unifieds";
		return Arrays.asList(
				restTemplate.postForEntity(url, new HttpEntity<>(keys, headers), PersonaKeyFacultad[].class).getBody());
	}

}
