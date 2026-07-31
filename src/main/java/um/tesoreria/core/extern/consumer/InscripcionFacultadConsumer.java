package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import um.tesoreria.core.extern.model.kotlin.InscripcionFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class InscripcionFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<InscripcionFacultad> findAllByLectivo(Integer facultadId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/inscripcion/lectivo/{facultadId}/{lectivoId}", facultadId, lectivoId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<InscripcionFacultad>>() {});
	}

	public List<InscripcionFacultad> findAllByCurso(Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer curso) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/inscripcion/curso/{facultadId}/{lectivoId}/{geograficaId}/{curso}",
						facultadId, lectivoId, geograficaId, curso)
				.retrieve()
				.body(new ParameterizedTypeReference<List<InscripcionFacultad>>() {});
	}

	public List<InscripcionFacultad> findAllByCursoSinProvisoria(Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer curso) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/inscripcion/cursosinprovisoria/{facultadId}/{lectivoId}/{geograficaId}/{curso}",
						facultadId, lectivoId, geograficaId, curso)
				.retrieve()
				.body(new ParameterizedTypeReference<List<InscripcionFacultad>>() {});
	}

	public InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}",
						facultadId, personaId, documentoId, lectivoId)
				.retrieve()
				.body(InscripcionFullDto.class);
	}

}
