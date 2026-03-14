/**
 * 
 */
package um.tesoreria.core.extern.consumer.view;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import um.tesoreria.core.extern.model.view.PreunivCarreraFacultad;

@Service
public class PreunivCarreraFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<PreunivCarreraFacultad> findAllByCarrera(String server, Long port, Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer turnoId, Integer planId, Integer carreraId) {
		String url = "http://" + server + ":" + port + "/preunivcarrera/carrera/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + turnoId + "/" + planId + "/" + carreraId;
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(PreunivCarreraFacultad[].class).getBody()));
	}

	public List<PreunivCarreraFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
			Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/preunivcarrera/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(PreunivCarreraFacultad[].class).getBody()));
	}

}
