package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.extern.consumer.PreInscripcionFacultadConsumer;
import um.tesoreria.core.extern.model.kotlin.PreInscripcionFacultad;
import um.tesoreria.core.hexagonal.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.FindPreInscriptosSinChequeraUseCase;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.service.view.PersonaKeyService;

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

    private final FacultadService facultadService;
    private final PreInscripcionFacultadConsumer preInscripcionFacultadConsumer;
    private final ChequeraSerieService chequeraSerieService;
    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findAllPreInscriptosSinChequera(Integer facultadId, Integer lectivoId,
            Integer geograficaId) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Map<String, PreInscripcionFacultad> preinscriptos = preInscripcionFacultadConsumer
                .findAllByPreInscriptos(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
                        geograficaId)
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
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
    }
}
