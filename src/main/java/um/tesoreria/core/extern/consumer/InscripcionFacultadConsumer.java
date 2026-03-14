/**
 * 
 */
package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import um.tesoreria.core.extern.model.kotlin.InscripcionFacultad;

@Service
public class InscripcionFacultadConsumer {

	private final RestClient restClient = RestClient.create();

	public List<InscripcionFacultad> findAllByLectivo(String server, Long port, Integer facultadId, Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/inscripcion/lectivo/" + facultadId + "/" + lectivoId;
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(InscripcionFacultad[].class).getBody()));
	}

	public List<InscripcionFacultad> findAllByCurso(String server, Long port, Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer curso) {
		String url = "http://" + server + ":" + port + "/inscripcion/curso/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + curso;
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(InscripcionFacultad[].class).getBody()));
	}

	public List<InscripcionFacultad> findAllByCursoSinProvisoria(String server, Long port, Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer curso) {
		String url = "http://" + server + ":" + port + "/inscripcion/cursosinprovisoria/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + curso;
		return Arrays.asList(Objects.requireNonNull(restClient.get().uri(url).retrieve().toEntity(InscripcionFacultad[].class).getBody()));
	}

	public InscripcionFullDto findInscripcionFull(String server, Long port, Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
		String url = "http://" + server + ":" + port + "/inscripcion/full/" + facultadId + "/" + personaId + "/" + documentoId + "/" + lectivoId;
		return restClient.get().uri(url).retrieve().body(InscripcionFullDto.class);
	}

}
