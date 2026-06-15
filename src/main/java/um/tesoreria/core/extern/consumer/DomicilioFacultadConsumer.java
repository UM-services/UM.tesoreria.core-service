/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;

@Service
public class DomicilioFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public DomicilioEntity sincronize(String server, Long port, DomicilioEntity domicilio) {
		String url = "http://" + server + ":" + port + "/domicilio/sincronize";
		return restClient.post().uri(url).body(domicilio).retrieve().body(DomicilioEntity.class);
	}

	public DomicilioEntity findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		String url = "http://" + server + ":" + port + "/domicilio/" + personaId + "/" + documentoId;
		return restClient.get().uri(url).retrieve().body(DomicilioEntity.class);
	}

	public DomicilioEntity findPagadorByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		String url = "http://" + server + ":" + port + "/domicilio/pagador/" + personaId + "/" + documentoId;
		return restClient.get().uri(url).retrieve().body(DomicilioEntity.class);
	}

}
