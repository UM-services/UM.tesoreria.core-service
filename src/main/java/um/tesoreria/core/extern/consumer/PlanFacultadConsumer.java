package um.tesoreria.core.extern.consumer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.PlanFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class PlanFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<PlanFacultad> findAll(Integer facultadId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/plan/")
				.retrieve()
				.body(new ParameterizedTypeReference<List<PlanFacultad>>() {});
	}

	public PlanFacultad findByUnique(Integer facultadId, Integer planId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/plan/unique/{facultadId}/{planId}", facultadId, planId)
				.retrieve()
				.body(PlanFacultad.class);
	}

}
