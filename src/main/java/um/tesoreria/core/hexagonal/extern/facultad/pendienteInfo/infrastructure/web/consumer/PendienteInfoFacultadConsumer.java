package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.extern.resolver.FacultadUrlResolver;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.out.PendienteInfoRepository;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper.PendienteInfoFacultadMapper;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendienteInfoFacultadConsumer implements PendienteInfoRepository {

    private final RestClient restClient;
    private final FacultadUrlResolver urlResolver;
    private final PendienteInfoFacultadMapper mapper;
    private final Clock clock;

    /**
     * Una falla de {@code /pendienteinfo} (HTTP de error, JSON ilegible, facultad sin servidor) se propaga:
     * el caller decide cómo informarla. Una falla de {@code /materia/unique} solo deja el nombre en {@code null}.
     */
    @Override
    public Optional<InscripcionMaterias> findByAlumno(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                      BigDecimal personaId, Integer documentoId) {
        log.debug("Processing PendienteInfoFacultadConsumer.findByAlumno");
        String baseUrl = urlResolver.getBaseUrl(facultadId);
        PendienteInfoFacultadResponse response = restClient.get()
                .uri(baseUrl + "/pendienteinfo/info/{facultadId}/{lectivoId}/{geograficaId}/{personaId}/{documentoId}",
                        facultadId, lectivoId, geograficaId, personaId.toPlainString(), documentoId)
                .retrieve()
                .body(PendienteInfoFacultadResponse.class);
        return mapper.toDomain(response, new MateriaUniqueResolver(restClient, baseUrl, clock));
    }
}
