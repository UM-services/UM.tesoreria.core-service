/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import um.tesoreria.core.model.Localidad;

@Service
public class LocalidadFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public Localidad findByUnique(String server, Long port, Integer facultadId, Integer provinciaId,
                                  Integer localidadId) {
		String url = "http://" + server + ":" + port + "/localidad/unique/" + facultadId + "/" + provinciaId + "/"
				+ localidadId;
		return restClient.get().uri(url).retrieve().body(Localidad.class);
	}

}
