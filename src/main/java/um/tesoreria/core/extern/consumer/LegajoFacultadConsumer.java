/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.LegajoFacultad;

@Service
public class LegajoFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public LegajoFacultad findByPersona(String server, Long port, BigDecimal personaId, Integer documentoId,
										Integer facultadId) {
		String url = "http://" + server + ":" + port + "/legajo/persona/" + personaId + "/" + documentoId + "/"
				+ facultadId;
		return restClient.get().uri(url).retrieve().body(LegajoFacultad.class);
	}

}
