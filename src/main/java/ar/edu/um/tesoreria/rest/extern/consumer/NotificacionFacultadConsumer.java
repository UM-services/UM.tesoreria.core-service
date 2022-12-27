/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.consumer;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.model.NotificacionExamen;

/**
 * @author daniel
 *
 */
@Service
public class NotificacionFacultadConsumer {

	public void examen48horas(String server, Long port, BigDecimal personaId, Integer documentoId, Integer facultadId,
			List<NotificacionExamen> emails) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://" + server + ":" + port + "/notificacion/examen48horas/" + personaId + "/" + documentoId
				+ "/" + facultadId;
		restTemplate.postForObject(url, emails, NotificacionExamen[].class);
	}

}
