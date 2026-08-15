package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.legajo.application.exception.LegajoException;
import um.tesoreria.core.hexagonal.personas.legajo.application.service.LegajoService;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.util.Tool;

@Component
@Slf4j
@RequiredArgsConstructor
class PreuniversitarioLegajoManager {

    private final LegajoService legajoService;

    Legajo findOrCreate(PreuniversitarioChequeraContext context) {
        try {
            return legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(
                    context.facultadId(), context.personaId(), context.documentoId());
        } catch (LegajoException exception) {
            Legajo legajo = new Legajo(null, context.personaId(), context.documentoId(),
                    context.facultadId(), 0L, Tool.dateAbsoluteArgentina(), context.lectivoId(), null,
                    null, (byte) 1, context.geograficaId(), "", (byte) 0, null);
            return legajoService.add(legajo);
        }
    }
}
