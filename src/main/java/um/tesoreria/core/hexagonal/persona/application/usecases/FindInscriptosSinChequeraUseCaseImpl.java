package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.consumer.LegajoFacultadConsumer;
import um.tesoreria.core.extern.model.kotlin.InscripcionFacultad;
import um.tesoreria.core.extern.model.kotlin.LegajoFacultad;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.FindInscriptosSinChequeraUseCase;
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
public class FindInscriptosSinChequeraUseCaseImpl implements FindInscriptosSinChequeraUseCase {

    private final FacultadService facultadService;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;
    private final ChequeraSerieService chequeraSerieService;
    private final LegajoFacultadConsumer legajoFacultadConsumer;
    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findAllInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer curso) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Map<String, InscripcionFacultad> inscriptos = inscripcionFacultadConsumer
                .findAllByCursoSinProvisoria(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
                        geograficaId, curso)
                .stream().collect(Collectors.toMap(InscripcionFacultad::getPersonaKey, Function.identity(),
                        (inscripto, replacement) -> inscripto));
        // Elimina los que ya tengan chequera
        Map<String, InscripcionFacultad> pendientes = new HashMap<>();
        for (InscripcionFacultad inscripto : inscriptos.values()) {
            boolean add = true;
            try {
                ChequeraSerie chequeraSerie;
                if (facultadId == 15) {
                    chequeraSerie = chequeraSerieService
                            .findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                                    inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId,
                                    geograficaId);
                } else {
                    chequeraSerie = chequeraSerieService
                            .findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                                    inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId,
                                    geograficaId);
                }
                add = false;
            } catch (ChequeraSerieException e) {
                log.debug("Sin chequera");
            }
            // Verifica los alumnos de intercambio
            if (add) {
                try {
                    LegajoFacultad legajo = legajoFacultadConsumer.findByPersona(facultad.getApiserver(),
                            facultad.getApiport(), inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId);
                    if (legajo.getIntercambio() == 1) {
                        add = false;
                    }
                } catch (Exception e) {
                    log.debug("Sin legajo");
                }
            }
            // Agrega el alumno
            if (add) {
                pendientes.put(inscripto.getPersonaKey(), inscripto);
            }
        }
        return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
    }
}
