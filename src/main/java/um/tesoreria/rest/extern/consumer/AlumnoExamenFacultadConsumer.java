/**
 * 
 */
package um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author daniel
 *
 */
@Service
public class AlumnoExamenFacultadConsumer {

	public Integer cantidad48horas(String server, Long port, BigDecimal personaId, Integer documentoId,
			Integer facultadId) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/alumnoexamen/48horascantidad/" + personaId + "/" + documentoId
				+ "/" + facultadId;
		return restTemplate.getForObject(url, Integer.class);
	}

}
