/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.edu.um.tesoreria.rest.extern.exception.BajaFacultadNotFoundException;
import ar.edu.um.tesoreria.rest.extern.model.BajaFacultad;
import reactor.core.publisher.Mono;

/**
 * @author daniel
 *
 */
@Service
public class BajaFacultadConsumer {

	public BajaFacultad findByUnique(String server, Long port, Integer facultadId, BigDecimal personaId,
			Integer documentoId, Integer lectivoId) {
		String baseUrl = MessageFormat.format("http://{0}:{1,number,#}/baja", server, port);
		WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
		String request = MessageFormat.format("/unique/{0}/{1,number,#}/{2}/{3}", facultadId, personaId, documentoId,
				lectivoId);
		Mono<BajaFacultad> response = webClient.get().uri(request).retrieve()
				.onStatus(status -> status.value() == HttpStatus.BAD_REQUEST.value(),
						clientResponse -> Mono.error(new BajaFacultadNotFoundException()))
				.bodyToMono(BajaFacultad.class);
		return response.block();
	}
}
