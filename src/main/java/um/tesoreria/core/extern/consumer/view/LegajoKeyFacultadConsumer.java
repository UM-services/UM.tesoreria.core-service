/**
 * 
 */
package um.tesoreria.core.extern.consumer.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import um.tesoreria.core.extern.model.view.LegajoKeyFacultad;

/**
 * @author daniel
 *
 */
@Service
public class LegajoKeyFacultadConsumer {

	public List<LegajoKeyFacultad> findAllByFacultadAndKeys(String server, Long port, Integer facultadId,
			List<String> keys) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "http://" + server + ":" + port + "/legajokey/unifieds/" + facultadId;
		return Arrays.asList(
				restTemplate.postForEntity(url, new HttpEntity<>(keys, headers), LegajoKeyFacultad[].class).getBody());
	}

}
