/**
 * 
 */
package um.tesoreria.core.extern.consumer.view;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import um.tesoreria.core.extern.model.view.LegajoKeyFacultad;

@Service
public class LegajoKeyFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<LegajoKeyFacultad> findAllByFacultadAndKeys(String server, Long port, Integer facultadId,
			List<String> keys) {
		String url = "http://" + server + ":" + port + "/legajokey/unifieds/" + facultadId;
		return Arrays.asList(
                Objects.requireNonNull(restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).body(keys).retrieve().toEntity(LegajoKeyFacultad[].class).getBody()));
	}

}