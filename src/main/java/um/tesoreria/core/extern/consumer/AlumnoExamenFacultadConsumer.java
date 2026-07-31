package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;

@Service
@RequiredArgsConstructor
public class AlumnoExamenFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public Integer cantidad48horas(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/alumnoexamen/48horascantidad/{personaId}/{documentoId}/{facultadId}",
						personaId, documentoId, facultadId)
				.retrieve()
				.body(Integer.class);
	}

}
