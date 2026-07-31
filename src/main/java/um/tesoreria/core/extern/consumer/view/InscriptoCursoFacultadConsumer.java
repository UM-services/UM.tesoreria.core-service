package um.tesoreria.core.extern.consumer.view;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.view.InscriptoCursoFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class InscriptoCursoFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<InscriptoCursoFacultad> findAllByLectivo(Integer facultadId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/inscriptocurso/curso/{facultadId}/{lectivoId}", facultadId, lectivoId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<InscriptoCursoFacultad>>() {});
	}

}