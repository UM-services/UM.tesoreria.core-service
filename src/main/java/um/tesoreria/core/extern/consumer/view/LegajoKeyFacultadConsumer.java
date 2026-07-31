package um.tesoreria.core.extern.consumer.view;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.view.LegajoKeyFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class LegajoKeyFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<LegajoKeyFacultad> findAllByFacultadAndKeys(Integer facultadId, List<String> keys) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.post()
				.uri(baseUrl + "/legajokey/unifieds/{facultadId}", facultadId)
				.contentType(MediaType.APPLICATION_JSON)
				.body(keys)
				.retrieve()
				.body(new ParameterizedTypeReference<List<LegajoKeyFacultad>>() {});
	}

}