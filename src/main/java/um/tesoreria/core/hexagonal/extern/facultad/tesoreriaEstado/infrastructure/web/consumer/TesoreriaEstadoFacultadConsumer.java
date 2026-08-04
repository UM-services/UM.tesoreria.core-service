package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.out.TesoreriaEstadoRepository;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.dto.TesoreriaEstadoFacultadResponse;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.mapper.TesoreriaEstadoFacultadMapper;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TesoreriaEstadoFacultadConsumer implements TesoreriaEstadoRepository {

    private final RestClient restClient;
    private final FacultadUrlResolver urlResolver;
    private final TesoreriaEstadoFacultadMapper mapper;

    @Override
    public Optional<TesoreriaEstadoFacultad> findByUnique(Integer facultadId,
                                                          BigDecimal personaId,
                                                          Integer documentoId) {
        log.debug("Processing TesoreriaEstadoFacultadConsumer.findByUnique");
        String baseUrl = urlResolver.getBaseUrl(facultadId);
        log.debug("\n\nbaseUrl: {}\n\n", baseUrl);
        TesoreriaEstadoFacultadResponse response;
        try {
            response = restClient.get()
                    .uri(baseUrl + "/tesoreriaEstado/unique/{facultadId}/{personaId}/{documentoId}",
                            facultadId, personaId, documentoId)
                    .retrieve()
                    .body(TesoreriaEstadoFacultadResponse.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            throw e;
        }
        return Optional.ofNullable(mapper.toDomain(response));
    }
}
