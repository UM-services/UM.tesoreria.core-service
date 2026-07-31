package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.model.kotlin.InscripcionDetalleFacultad;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class InscripcionDetalleFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public List<InscripcionDetalleFacultad> findAllByPersona(Integer facultadId, BigDecimal personaId,
															 Integer documentoId, Integer lectivoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/inscripciondetalle/persona/{personaId}/{documentoId}/{facultadId}/{lectivoId}",
						personaId, documentoId, facultadId, lectivoId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<InscripcionDetalleFacultad>>() {});
	}

}
