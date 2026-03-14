/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.PreTurnoFacultad;

@Service
public class PreTurnoFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<PreTurnoFacultad> findAllByLectivo(String server, Long port, Integer facultadId, Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/preturno/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restClient.get().uri(url).retrieve().toEntity(PreTurnoFacultad[].class).getBody());
	}

}