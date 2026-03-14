/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import um.tesoreria.core.extern.exception.BajaFacultadException;
import um.tesoreria.core.extern.model.kotlin.BajaFacultad;

@Service
public class BajaFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public BajaFacultad findByUnique(String server, Long port, Integer facultadId, BigDecimal personaId,
									 Integer documentoId, Integer lectivoId) {
		String baseUrl = MessageFormat.format("http://{0}:{1,number,#}/baja", server, port);
		String requestPath = MessageFormat.format("/unique/{0}/{1,number,#}/{2}/{3}", facultadId, personaId, documentoId,
				lectivoId);
		return restClient.get()
				.uri(baseUrl + requestPath)
				.retrieve()
				.onStatus(status -> status.value() == 400,
						(req, response) -> { throw new BajaFacultadException(); })
				.body(BajaFacultad.class);
	}
}
