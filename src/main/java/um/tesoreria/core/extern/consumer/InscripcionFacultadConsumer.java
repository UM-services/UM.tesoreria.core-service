/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import um.tesoreria.core.extern.model.kotlin.InscripcionFacultad;

/**
 * @author daniel
 *
 */
@Service
public class InscripcionFacultadConsumer {

	public List<InscripcionFacultad> findAllByLectivo(String server, Long port, Integer facultadId, Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/inscripcion/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, InscripcionFacultad[].class).getBody());
	}

	public List<InscripcionFacultad> findAllByCurso(String server, Long port, Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer curso) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/inscripcion/curso/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + curso;
		return Arrays.asList(restTemplate.getForEntity(url, InscripcionFacultad[].class).getBody());
	}

	public List<InscripcionFacultad> findAllByCursoSinProvisoria(String server, Long port, Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer curso) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/inscripcion/cursosinprovisoria/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + curso;
		return Arrays.asList(restTemplate.getForEntity(url, InscripcionFacultad[].class).getBody());
	}

}
