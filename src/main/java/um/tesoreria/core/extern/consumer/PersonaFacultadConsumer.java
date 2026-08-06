package um.tesoreria.core.extern.consumer;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaEntity;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonaFacultadConsumer {

	private final RestClient restClient;
	private final FacultadUrlResolver urlResolver;

	public PersonaEntity findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		String baseUrl = urlResolver.getBaseUrl(facultadId);
		log.debug("baseUrl -> {}, personaId -> {}, documentoId -> {}", baseUrl, personaId, documentoId);
		return restClient.get()
				.uri(baseUrl + "/persona/{personaId}/{documentoId}", personaId, documentoId)
				.retrieve()
				.body(PersonaEntity.class);
	}

}