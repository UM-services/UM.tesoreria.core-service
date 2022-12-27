/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.model.Persona;

/**
 * @author daniel
 *
 */
@Service
public class PersonaFacultadConsumer {

	public Persona findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/persona/" + personaId + "/" + documentoId;
		return restTemplate.getForObject(url, Persona.class);
	}

}
