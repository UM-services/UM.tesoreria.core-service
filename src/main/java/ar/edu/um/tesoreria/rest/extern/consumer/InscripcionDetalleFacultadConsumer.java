/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import ar.edu.um.tesoreria.rest.extern.model.kotlin.InscripcionDetalleFacultad;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author daniel
 *
 */
@Service
public class InscripcionDetalleFacultadConsumer {

	public List<InscripcionDetalleFacultad> findAllByPersona(String server, Long port, BigDecimal personaId,
															 Integer documentoId, Integer facultadId, Integer lectivoId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/inscripciondetalle/persona/" + personaId + "/" + documentoId
				+ "/" + facultadId + "/" + lectivoId;
		return Arrays.asList(restTemplate.getForEntity(url, InscripcionDetalleFacultad[].class).getBody());
	}

}
