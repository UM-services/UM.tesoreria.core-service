package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CheckAllPreuniversitarioWithoutChequeraUseCase;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoDeteccionRequest;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckAllPreuniversitarioWithoutChequeraUseCaseImpl implements CheckAllPreuniversitarioWithoutChequeraUseCase {

    private final ChequeraSerieService chequeraSerieService;
    private final LectivoService lectivoService;

    @Override
    public List<AlumnoDeteccionRequest> desmarcarEnviados(List<AlumnoDeteccionRequest> encontrados) {
        log.debug("\n\nProcessing CheckAllPreuniversitarioWithoutChequeraUseCaseImpl.desmarcarEnviados\n\n");
        List<AlumnoDeteccionRequest> pendientes = new ArrayList<>();
        var lectivoId = lectivoService.findByFecha(OffsetDateTime.now()).getLectivoId();
        for (var encontrado : encontrados) {
            var context = Tool.convert2Tesium(encontrado.getUbicacion(), encontrado.getPropuesta());
            log.debug("Context: {}", context.jsonify());
            var personaId = new BigDecimal(encontrado.getNroDocumento());
            var documentoId = Integer.valueOf(encontrado.getTipoDocumento() == 0 ? 1 : encontrado.getTipoDocumento());
            try {
                chequeraSerieService.findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
                        personaId,
                        documentoId,
                        context.getFacultadId(),
                        lectivoId,
                        context.getGeograficaId(),
                        context.getTipoChequeraId()
                );
                log.debug("\n\nChequera encontrada\n\n");
            } catch (ChequeraSerieException e) {
                log.error(e.getMessage());
                pendientes.add(AlumnoDeteccionRequest.builder()
                        .alumno(encontrado.getAlumno())
                        .ubicacion(encontrado.getUbicacion())
                        .propuesta(encontrado.getPropuesta())
                        .nroDocumento(encontrado.getNroDocumento())
                        .tipoDocumento(encontrado.getTipoDocumento())
                        .pendiente(true)
                        .build());
            }
        }
        return pendientes;
    }

}
