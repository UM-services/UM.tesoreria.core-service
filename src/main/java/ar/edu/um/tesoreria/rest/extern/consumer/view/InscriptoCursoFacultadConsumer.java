/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer.view;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.extern.model.view.InscriptoCursoFacultad;

/**
 * @author daniel
 *
 */
@Service
public class InscriptoCursoFacultadConsumer {

	public List<InscriptoCursoFacultad> findAllByLectivo(String server, Long port, Integer facultadId,
			Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/inscriptocurso/curso/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, InscriptoCursoFacultad[].class).getBody());
	}

}
