package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto.PendienteInfoFacultadResponse.Materia;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper.NombreMateriaResolver;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Pide a {@code GET /materia/unique} el nombre de las materias que {@code /pendienteinfo} no trae.
 * Una instancia sirve a una sola consulta. Tiene tres topes:
 * <ol>
 *   <li>Como máximo {@value #MAX_LLAMADAS} llamadas.</li>
 *   <li>Deja de llamar cuando pasan {@code PRESUPUESTO} desde su creación, medidos con el {@link Clock} del core.
 *   El tope decide si empieza una llamada. No acota la duración de una llamada en curso: eso lo hace el
 *   timeout del {@link RestClient} global.</li>
 *   <li>Deja de llamar después de la primera falla.</li>
 * </ol>
 * Una falla no se propaga: deja el nombre en {@code null}. El log lleva la clave de la materia y la clase de la
 * excepción, nunca su mensaje.
 */
@Slf4j
class MateriaUniqueResolver implements NombreMateriaResolver {

    static final int MAX_LLAMADAS = 10;
    static final Duration PRESUPUESTO = Duration.ofSeconds(3);

    private final RestClient restClient;
    private final String baseUrl;
    private final Clock clock;
    private final Instant inicio;

    private int llamadas;
    private boolean fallo;

    MateriaUniqueResolver(RestClient restClient, String baseUrl, Clock clock) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
        this.clock = clock;
        this.inicio = clock.instant();
    }

    @Override
    public String resolver(Integer facultadId, Integer planId, String materiaId) {
        if (fallo || llamadas >= MAX_LLAMADAS || !Duration.between(inicio, clock.instant()).minus(PRESUPUESTO).isNegative()) {
            return null;
        }
        llamadas++;
        try {
            Materia materia = restClient.get()
                    .uri(baseUrl + "/materia/unique/{facultadId}/{planId}/{materiaId}", facultadId, planId, materiaId)
                    .retrieve()
                    .body(Materia.class);
            return materia == null ? null : materia.nombre();
        } catch (RuntimeException e) {
            fallo = true;
            log.warn("No se pudo buscar el nombre de la materia {}/{}/{}: {}",
                    facultadId, planId, materiaId, e.getClass().getSimpleName());
            return null;
        }
    }
}
