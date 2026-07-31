package um.tesoreria.core.extern.consumer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.PreTurnoFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class PreTurnoFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<PreTurnoFacultad> findAllByLectivo(Integer facultadId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/preturno/lectivo/{facultadId}/{lectivoId}", facultadId, lectivoId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<PreTurnoFacultad>>() {});
	}

}