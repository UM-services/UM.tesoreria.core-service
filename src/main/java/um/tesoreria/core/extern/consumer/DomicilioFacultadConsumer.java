/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.kotlin.model.Domicilio;

@Service
public class DomicilioFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public Domicilio sincronize(String server, Long port, Domicilio domicilio) {
		String url = "http://" + server + ":" + port + "/domicilio/sincronize";
		return restClient.post().uri(url).body(domicilio).retrieve().body(Domicilio.class);
	}

	public Domicilio findByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		String url = "http://" + server + ":" + port + "/domicilio/" + personaId + "/" + documentoId;
		return restClient.get().uri(url).retrieve().body(Domicilio.class);
	}

	public Domicilio findPagadorByUnique(String server, Long port, BigDecimal personaId, Integer documentoId) {
		String url = "http://" + server + ":" + port + "/domicilio/pagador/" + personaId + "/" + documentoId;
		return restClient.get().uri(url).retrieve().body(Domicilio.class);
	}

}
