/**
 * 
 */
package um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.rest.kotlin.model.Domicilio;

/**
 * @author daniel
 *
 */
@Service
public class DomicilioFacultadConsumer {

	public Domicilio sincronize(String server, Long port, Domicilio domicilio) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/domicilio/sincronize";
		return restTemplate.postForObject(url, domicilio, Domicilio.class);
	}

	public Domicilio findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/domicilio/" + personaId + "/" + documentoId;
		return restTemplate.getForObject(url, Domicilio.class);
	}

	public Domicilio findPagadorByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/domicilio/pagador/" + personaId + "/" + documentoId;
		return restTemplate.getForObject(url, Domicilio.class);
	}

}
