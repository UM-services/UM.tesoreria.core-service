/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AlumnoExamenFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public Integer cantidad48horas(String server, Long port, BigDecimal personaId, Integer documentoId,
			Integer facultadId) {
		String url = "http://" + server + ":" + port + "/alumnoexamen/48horascantidad/" + personaId + "/" + documentoId
				+ "/" + facultadId;
		return restClient.get()
			.uri(url)
			.retrieve()
			.body(Integer.class);
	}

}
