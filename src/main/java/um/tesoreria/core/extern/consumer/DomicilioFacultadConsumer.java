package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;

@Service
@RequiredArgsConstructor
public class DomicilioFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public DomicilioEntity sincronize(Integer facultadId, DomicilioEntity domicilio) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.post()
				.uri(baseUrl + "/domicilio/sincronize")
				.body(domicilio)
				.retrieve()
				.body(DomicilioEntity.class);
	}

	public DomicilioEntity findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/domicilio/{personaId}/{documentoId}", personaId, documentoId)
				.retrieve()
				.body(DomicilioEntity.class);
	}

	public DomicilioEntity findPagadorByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/domicilio/pagador/{personaId}/{documentoId}", personaId, documentoId)
				.retrieve()
				.body(DomicilioEntity.class);
	}

}
