package um.tesoreria.core.extern.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.model.Localidad;

@Service
@RequiredArgsConstructor
public class LocalidadFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public Localidad findByUnique(Integer facultadId, Integer provinciaId, Integer localidadId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/localidad/unique/{facultadId}/{provinciaId}/{localidadId}",
						facultadId, provinciaId, localidadId)
				.retrieve()
				.body(Localidad.class);
	}

}
