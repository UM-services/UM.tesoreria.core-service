package um.tesoreria.core.extern.consumer.view;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.view.PreunivCarreraFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class PreunivCarreraFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<PreunivCarreraFacultad> findAllByCarrera(Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer turnoId, Integer planId, Integer carreraId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/preunivcarrera/carrera/{facultadId}/{lectivoId}/{geograficaId}/{turnoId}/{planId}/{carreraId}",
						facultadId, lectivoId, geograficaId, turnoId, planId, carreraId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<PreunivCarreraFacultad>>() {});
	}

	public List<PreunivCarreraFacultad> findAllByLectivo(Integer facultadId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/preunivcarrera/lectivo/{facultadId}/{lectivoId}", facultadId, lectivoId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<PreunivCarreraFacultad>>() {});
	}

}
