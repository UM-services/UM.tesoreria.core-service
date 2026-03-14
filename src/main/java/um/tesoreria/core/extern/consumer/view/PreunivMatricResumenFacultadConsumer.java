/**
 * 
 */
package um.tesoreria.core.extern.consumer.view;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import um.tesoreria.core.extern.model.view.PreunivMatricResumenFacultad;

@Service
public class PreunivMatricResumenFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<PreunivMatricResumenFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
			Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/preunivmatricresumen/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(PreunivMatricResumenFacultad[].class).getBody()));
	}

}