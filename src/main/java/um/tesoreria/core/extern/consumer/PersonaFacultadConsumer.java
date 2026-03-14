/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;

@Service
@Slf4j
public class PersonaFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public PersonaEntity findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		String url = "http://" + server + ":" + port + "/persona/" + personaId + "/" + documentoId;
		log.debug("url -> {}", url);
		return restClient.get().uri(url).retrieve().body(PersonaEntity.class);
	}

}