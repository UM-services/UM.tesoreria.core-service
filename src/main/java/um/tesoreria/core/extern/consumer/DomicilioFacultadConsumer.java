package um.tesoreria.core.extern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.entity.DomicilioEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomicilioFacultadConsumer {

	private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
			.findAndAddModules()
			.build();

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
		String url = baseUrl + "/domicilio/" + personaId + "/" + documentoId;
		log.info("CONSUMER[DomicilioFacultad] -> GET {}", url);
		String raw = restClient.get()
				.uri(baseUrl + "/domicilio/{personaId}/{documentoId}", personaId, documentoId)
				.retrieve()
				.body(String.class);
		log.info("CONSUMER[DomicilioFacultad] -> RAW de {}: {}", url,
				raw == null ? "NULL (sin body)" : raw);
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			DomicilioEntity response = OBJECT_MAPPER.readValue(raw, DomicilioEntity.class);
			log.info("CONSUMER[DomicilioFacultad] -> parseada de {}: {}", url, response.jsonify());
			return response;
		} catch (JsonProcessingException e) {
			log.error("CONSUMER[DomicilioFacultad] -> no se pudo parsear la respuesta de {}: {}", url, e.getMessage());
			throw new IllegalStateException("Respuesta no parseable de " + url, e);
		}
	}

	public DomicilioEntity findPagadorByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		return restClient.get()
				.uri(baseUrl + "/domicilio/pagador/{personaId}/{documentoId}", personaId, documentoId)
				.retrieve()
				.body(DomicilioEntity.class);
	}

}
