package um.tesoreria.core.extern.consumer.view;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.view.PersonaKeyFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class PersonaKeyFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<PersonaKeyFacultad> findAllByUnifieds(Integer facultadId, List<String> keys) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.post()
				.uri(baseUrl + "/personakey/unifieds")
				.contentType(MediaType.APPLICATION_JSON)
				.body(keys)
				.retrieve()
				.body(new ParameterizedTypeReference<List<PersonaKeyFacultad>>() {});
	}

}
