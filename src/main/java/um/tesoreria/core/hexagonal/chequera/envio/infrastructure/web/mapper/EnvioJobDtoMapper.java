package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobDetalle;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioOperacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.FiltroEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobDetalleResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobRequest;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobTareaResponse;

import java.util.List;

@Component
public class EnvioJobDtoMapper {

    public FiltroEnvio toFiltro(EnvioJobRequest request) {
        if (request == null) {
            return FiltroEnvio.builder().build();
        }
        return FiltroEnvio.builder()
                .operacion(toOperacion(request.getOperacion()))
                .lectivoId(request.getLectivoId())
                .facultadId(request.getFacultadId())
                .geograficaId(request.getGeograficaId())
                .soloNoEnviadas(request.isSoloNoEnviadas())
                .chequeraIds(request.getChequeraIds())
                .codigoBarras(request.isCodigoBarras())
                .build();
    }

    public EnvioJobResponse toResponse(EnvioJob job) {
        if (job == null) {
            return null;
        }
        return EnvioJobResponse.builder()
                .jobId(job.getJobId())
                .operacion(job.getOperacion() == null ? null : job.getOperacion().name())
                .lectivoId(job.getLectivoId())
                .facultadId(job.getFacultadId())
                .geograficaId(job.getGeograficaId())
                .soloNoEnviadas(job.isSoloNoEnviadas())
                .codigoBarras(job.isCodigoBarras())
                .estado(job.getEstado() == null ? null : job.getEstado().name())
                .totalTareas(job.getTotalTareas())
                .tareasOk(job.getTareasOk())
                .tareasOmitidas(job.getTareasOmitidas())
                .tareasError(job.getTareasError())
                .tareasPendientes(job.getTareasPendientes())
                .usuarioId(job.getUsuarioId())
                .usuarioLogin(job.getUsuarioLogin())
                .iniciado(job.getIniciado())
                .finalizado(job.getFinalizado())
                .mensaje(job.getMensaje())
                .ultimaChequeraId(job.getUltimaChequeraId())
                .build();
    }

    public EnvioJobTareaResponse toResponse(EnvioJobTarea tarea) {
        if (tarea == null) {
            return null;
        }
        return EnvioJobTareaResponse.builder()
                .tareaId(tarea.getTareaId())
                .chequeraId(tarea.getChequeraId())
                .numero(tarea.getFacultadId() + "/" + tarea.getTipoChequeraId() + "/" + tarea.getChequeraSerieId())
                .personaId(tarea.getPersonaId())
                .documentoId(tarea.getDocumentoId())
                .personaNombre(tarea.getPersonaNombre())
                .estado(tarea.getEstado() == null ? null : tarea.getEstado().name())
                .codigo(tarea.getCodigo())
                .mensaje(tarea.getMensaje())
                .detalle(tarea.getDetalle())
                .retenida(tarea.getRetenida())
                .enviado(tarea.getEnviado())
                .finalizado(tarea.getFinalizado())
                .build();
    }

    public EnvioJobDetalleResponse toResponse(EnvioJobDetalle detalle) {
        if (detalle == null) {
            return null;
        }
        List<EnvioJobTareaResponse> tareas = detalle.getTareas() == null ? List.of()
                : detalle.getTareas().stream().map(this::toResponse).toList();
        return EnvioJobDetalleResponse.builder()
                .job(toResponse(detalle.getJob()))
                .tareas(tareas)
                .build();
    }

    private static EnvioOperacion toOperacion(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return EnvioOperacion.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
