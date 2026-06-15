package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.consumer.LegajoFacultadConsumer;
import um.tesoreria.core.extern.model.kotlin.InscripcionFacultad;
import um.tesoreria.core.extern.model.kotlin.LegajoFacultad;
import um.tesoreria.core.hexagonal.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.FindInscriptosSinChequeraDefaultUseCase;
import um.tesoreria.core.kotlin.model.CarreraChequera;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.service.CarreraChequeraService;
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
public class FindInscriptosSinChequeraDefaultUseCaseImpl implements FindInscriptosSinChequeraDefaultUseCase {

    private final FacultadService facultadService;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;
    private final CarreraChequeraService carreraChequeraService;
    private final ChequeraSerieService chequeraSerieService;
    private final LegajoFacultadConsumer legajoFacultadConsumer;
    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findAllInscriptosSinChequeraDefault(Integer facultadId, Integer lectivoId,
            Integer geograficaId, Integer claseChequeraId, Integer curso) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Map<String, InscripcionFacultad> inscriptos = inscripcionFacultadConsumer
                .findAllByCursoSinProvisoria(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
                        geograficaId, curso)
                .stream().collect(Collectors.toMap(InscripcionFacultad::getPersonaKey, Function.identity(),
                        (inscripto, replacemente) -> inscripto));
        Map<String, CarreraChequera> tipos = carreraChequeraService
                .findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(facultadId, lectivoId,
                        geograficaId, claseChequeraId, curso)
                .stream().collect(Collectors.toMap(CarreraChequera::getCarreraKey, Function.identity(),
                        (tipo, replacement) -> tipo));
        // Elimina los que ya tengan chequera
        Map<String, InscripcionFacultad> pendientes = new HashMap<>();
        for (InscripcionFacultad inscripto : inscriptos.values()) {
            CarreraChequera carreraChequera = null;
            if (!tipos.containsKey(facultadId + "." + inscripto.getPlanId() + "." + inscripto.getCarreraId()))
                continue;
            carreraChequera = tipos.get(facultadId + "." + inscripto.getPlanId() + "." + inscripto.getCarreraId());
            boolean add = true;
            try {
                var chequeraSerie = chequeraSerieService
                        .findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
                                inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId,
                                geograficaId, carreraChequera.getTipoChequeraId());
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
