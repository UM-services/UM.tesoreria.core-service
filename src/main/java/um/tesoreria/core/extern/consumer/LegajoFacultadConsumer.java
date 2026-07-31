package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.LegajoFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class LegajoFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public LegajoFacultad findByPersona(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/legajo/persona/{personaId}/{documentoId}/{facultadId}",
						personaId, documentoId, facultadId)
				.retrieve()
				.body(LegajoFacultad.class);
	}

}
