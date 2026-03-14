/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.CarreraFacultad;

@Service
public class CarreraFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<CarreraFacultad> findAll(String server, Long port) {
		String url = "http://" + server + ":" + port + "/carrera/";
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(CarreraFacultad[].class).getBody()));
	}

	public CarreraFacultad findByUnique(String server, Long port, Integer facultadId, Integer planId,
			Integer carreraId) {
		String url = "http://" + server + ":" + port + "/carrera/unique/" + facultadId + "/" + planId + "/" + carreraId;
		return restClient.get().uri(url).retrieve().body(CarreraFacultad.class);
	}

}
