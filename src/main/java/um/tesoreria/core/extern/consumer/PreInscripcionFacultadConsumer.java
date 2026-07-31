package um.tesoreria.core.extern.consumer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.PreInscripcionFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class PreInscripcionFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<PreInscripcionFacultad> findAllByLectivo(Integer facultadId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/preinscripcion/lectivo/{facultadId}/{lectivoId}", facultadId, lectivoId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<PreInscripcionFacultad>>() {});
	}

	public List<PreInscripcionFacultad> findAllByPreInscriptos(Integer facultadId, Integer lectivoId, Integer geograficaId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/preinscripcion/sede/{facultadId}/{lectivoId}/{geograficaId}",
						facultadId, lectivoId, geograficaId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<PreInscripcionFacultad>>() {});
	}

}
