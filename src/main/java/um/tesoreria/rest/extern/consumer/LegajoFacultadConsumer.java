/**
 * 
 */
package um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.rest.extern.model.kotlin.LegajoFacultad;

/**
 * @author daniel
 *
 */
@Service
public class LegajoFacultadConsumer {

	public LegajoFacultad findByPersona(String server, Long port, BigDecimal personaId, Integer documentoId,
										Integer facultadId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/legajo/persona/" + personaId + "/" + documentoId + "/"
				+ facultadId;
		return restTemplate.getForObject(url, LegajoFacultad.class);
	}

}
