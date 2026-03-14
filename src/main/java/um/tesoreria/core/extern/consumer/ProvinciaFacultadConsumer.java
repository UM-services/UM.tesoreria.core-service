/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import um.tesoreria.core.model.Provincia;

@Service
public class ProvinciaFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public Provincia findByUnique(String server, Long port, Integer facultadId, Integer provinciaId) {
		String url = "http://" + server + ":" + port + "/provincia/unique/" + facultadId + "/" + provinciaId;
		return restClient.get().uri(url).retrieve().body(Provincia.class);
	}

}