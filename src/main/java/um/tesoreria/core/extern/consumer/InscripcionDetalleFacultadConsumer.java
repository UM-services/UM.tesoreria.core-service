/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.InscripcionDetalleFacultad;

@Service
public class InscripcionDetalleFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<InscripcionDetalleFacultad> findAllByPersona(String server, Long port, BigDecimal personaId,
															 Integer documentoId, Integer facultadId, Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/inscripciondetalle/persona/" + personaId + "/" + documentoId
				+ "/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restClient.get().uri(url).retrieve().toEntity(InscripcionDetalleFacultad[].class).getBody());
	}

}
