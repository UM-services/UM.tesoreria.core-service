package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroIncompletas;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobDetalle;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobDetalleResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobTareaResponse;

@Component
public class IncompletasDtoMapper {

    public FiltroIncompletas toFiltro(IncompletasJobRequest request) {
        return FiltroIncompletas.builder()
                .lectivoId(request.lectivoId())
                .facultadId(request.facultadId())
                .geograficaId(request.geograficaId())
                .claseChequeraId(request.claseChequeraId())
                .soloNoEnviadas(Boolean.TRUE.equals(request.soloNoEnviadas()))
                .chequeraIds(request.chequeraIds())
                .build();
    }

    public IncompletasJobResponse toResponse(IncompletasJob job) {
        return IncompletasJobResponse.builder()
                .jobId(job.getJobId())
                .etapa(job.getEtapa() == null ? null : job.getEtapa().name())
                .jobOrigenId(job.getJobOrigenId())
                .lectivoId(job.getLectivoId())
                .facultadId(job.getFacultadId())
                .geograficaId(job.getGeograficaId())
                .claseChequeraId(job.getClaseChequeraId())
                .soloNoEnviadas(job.isSoloNoEnviadas())
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

    public IncompletasJobTareaResponse toResponse(IncompletasJobTarea tarea) {
        return IncompletasJobTareaResponse.builder()
                .tareaId(tarea.getTareaId())
                .chequeraId(tarea.getChequeraId())
                .personaId(tarea.getPersonaId())
                .documentoId(tarea.getDocumentoId())
                .facultadId(tarea.getFacultadId())
                .tipoChequeraId(tarea.getTipoChequeraId())
                .chequeraSerieId(tarea.getChequeraSerieId())
                .personaNombre(tarea.getPersonaNombre())
                .borradorId(tarea.getBorradorId())
                .estado(tarea.getEstado() == null ? null : tarea.getEstado().name())
                .codigo(tarea.getCodigo())
                .mensaje(tarea.getMensaje())
                .finalizado(tarea.getFinalizado())
                .build();
    }

    public IncompletasJobDetalleResponse toResponse(IncompletasJobDetalle detalle) {
        return IncompletasJobDetalleResponse.builder()
                .job(toResponse(detalle.getJob()))
                .tareas(detalle.getTareas().stream().map(this::toResponse).toList())
                .build();
    }
}
