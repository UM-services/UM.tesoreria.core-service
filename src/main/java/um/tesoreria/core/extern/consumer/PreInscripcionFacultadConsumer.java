/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.PreInscripcionFacultad;

@Service
public class PreInscripcionFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<PreInscripcionFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
														 Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/preinscripcion/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restClient.get().uri(url).retrieve().toEntity(PreInscripcionFacultad[].class).getBody());
	}

	public List<PreInscripcionFacultad> findAllByPreInscriptos(String server, Long port, Integer facultadId,
			Integer lectivoId, Integer geograficaId) {
		String url = "http://" + server + ":" + port + "/preinscripcion/sede/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId;
		return Arrays.asList(restClient.get().uri(url).retrieve().toEntity(PreInscripcionFacultad[].class).getBody());
	}

}
