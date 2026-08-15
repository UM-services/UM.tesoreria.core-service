package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.extern.consumer.PreInscripcionFacultadConsumer;
import um.tesoreria.core.extern.model.kotlin.PreInscripcionFacultad;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.FindPreInscriptosSinChequeraUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindPreInscriptosSinChequeraUseCaseImpl implements FindPreInscriptosSinChequeraUseCase {

    private final PreInscripcionFacultadConsumer preInscripcionFacultadConsumer;
    private final ChequeraSerieService chequeraSerieService;
    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findAllPreInscriptosSinChequera(Integer facultadId, Integer lectivoId,
            Integer geograficaId) {
        Map<String, PreInscripcionFacultad> preinscriptos = preInscripcionFacultadConsumer
                .findAllByPreInscriptos(facultadId, lectivoId, geograficaId)
                .stream().collect(Collectors.toMap(PreInscripcionFacultad::getPersonaKey, Function.identity(),
                        (preinscripto, replacement) -> preinscripto));
        // Elimina los que ya tengan chequera
        Map<String, PreInscripcionFacultad> pendientes = new HashMap<>();
        for (PreInscripcionFacultad preinscripto : preinscriptos.values()) {
            boolean add = true;
            try {
                var chequeraSerie = chequeraSerieService
                        .findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                                preinscripto.getPersonaId(), preinscripto.getDocumentoId(), facultadId, lectivoId - 1,
                                geograficaId);
                log.debug("ChequeraSerie -> {}", chequeraSerie.jsonify());
                add = false;
            } catch (ChequeraSerieException e) {
                log.debug("Sin chequera");
            }
            // Agrega el alumno
            if (add) {
                pendientes.put(preinscripto.getPersonaKey(), preinscripto);
            }
        }
        return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
                List.of("apellido", "nombre"));
    }
}
