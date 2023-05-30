/**
 * 
 */
package um.tesoreria.rest.extern.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import um.tesoreria.rest.model.Localidad;
import um.tesoreria.rest.model.Localidad;

/**
 * @author daniel
 *
 */
@Service
public class LocalidadFacultadConsumer {

	public Localidad findByUnique(String server, Long port, Integer facultadId, Integer provinciaId,
                                  Integer localidadId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/localidad/unique/" + facultadId + "/" + provinciaId + "/"
				+ localidadId;
		return restTemplate.getForObject(url, Localidad.class);
	}

}
