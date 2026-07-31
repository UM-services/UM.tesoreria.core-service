package um.tesoreria.core.extern.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.model.Provincia;

@Service
@RequiredArgsConstructor
public class ProvinciaFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public Provincia findByUnique(Integer facultadId, Integer provinciaId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/provincia/unique/{facultadId}/{provinciaId}", facultadId, provinciaId)
				.retrieve()
				.body(Provincia.class);
	}

}