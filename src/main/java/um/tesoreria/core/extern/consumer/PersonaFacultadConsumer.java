/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class PersonaFacultadConsumer {

	public PersonaEntity findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/persona/" + personaId + "/" + documentoId;
		log.debug("url -> {}", url);
		return restTemplate.getForObject(url, PersonaEntity.class);
	}

}
