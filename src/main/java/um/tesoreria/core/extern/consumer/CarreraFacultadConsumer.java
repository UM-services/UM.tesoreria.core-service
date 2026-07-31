package um.tesoreria.core.extern.consumer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.CarreraFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class CarreraFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<CarreraFacultad> findAll(Integer facultadId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/carrera/")
				.retrieve()
				.body(new ParameterizedTypeReference<List<CarreraFacultad>>() {});
	}

	public CarreraFacultad findByUnique(Integer facultadId, Integer planId, Integer carreraId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/carrera/unique/{facultadId}/{planId}/{carreraId}", facultadId, planId, carreraId)
				.retrieve()
				.body(CarreraFacultad.class);
	}

}
