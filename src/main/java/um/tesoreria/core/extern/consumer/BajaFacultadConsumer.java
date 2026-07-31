package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.exception.BajaFacultadException;
import um.tesoreria.core.extern.model.kotlin.BajaFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class BajaFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public BajaFacultad findByUnique(Integer facultadId, BigDecimal personaId,
									 Integer documentoId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/baja/unique/{facultadId}/{personaId}/{documentoId}/{lectivoId}",
						facultadId, personaId, documentoId, lectivoId)
				.retrieve()
				.onStatus(status -> status.value() == 400,
						(req, response) -> { throw new BajaFacultadException(); })
				.body(BajaFacultad.class);
	}

}
