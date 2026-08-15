package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.exception.GeograficaException;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.exception.TipoChequeraException;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.service.GuaraniPropuestaTipoChequeraService;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.service.GuaraniUbicacionService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;
import um.tesoreria.core.kotlin.model.Build;
import um.tesoreria.core.service.BuildService;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
class PreuniversitarioDataResolver {

    private final LectivoService lectivoService;
    private final BuildService buildService;
    private final GuaraniPropuestaTipoChequeraService guaraniPropuestaTipoChequeraService;
    private final FacultadService facultadService;
    private final GuaraniUbicacionService guaraniUbicacionService;

    Optional<PreuniversitarioChequeraContext> resolve(PreuniversitarioChequeraData data) {
        int lectivoId = lectivoService.findByFecha(OffsetDateTime.now()).getLectivoId();
        Build build = buildService.findLast();

        int tipoChequeraId;
        try {
            tipoChequeraId = guaraniPropuestaTipoChequeraService
                    .findByPropuestaAndLectivo(data.propuesta(), lectivoId)
                    .getTipoChequeraId();
        } catch (TipoChequeraException exception) {
            log.error("No se encontró el tipo de chequera: {}", exception.getMessage());
            return Optional.empty();
        }

        int facultadId;
        try {
            facultadId = facultadService
                    .findByGuaraniResponsableAcademica(data.responsableAcademica())
                    .getFacultadId();
        } catch (FacultadException exception) {
            log.error("No se encontró la facultad: {}", exception.getMessage());
            return Optional.empty();
        }

        int geograficaId;
        try {
            geograficaId = guaraniUbicacionService
                    .findByUbicacion(data.ubicacion())
                    .getGeograficaId();
        } catch (GeograficaException exception) {
            log.error("No se encontró la geográfica: {}", exception.getMessage());
            return Optional.empty();
        }

        return Optional.of(new PreuniversitarioChequeraContext(
                lectivoId,
                tipoChequeraId,
                facultadId,
                geograficaId,
                data.personaId(),
                data.documentoId(),
                build));
    }
}
