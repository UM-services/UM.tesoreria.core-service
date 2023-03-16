/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.model.kotlin.Persona;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class PersonaFacultadConsumer {

	public Persona findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/persona/" + personaId + "/" + documentoId;
		log.debug("url -> {}", url);
		return restTemplate.getForObject(url, Persona.class);
	}

}
