package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroGeneracion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobDetalle;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionPadronItem;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionFiltroRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionJobDetalleResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionJobResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionJobTareaResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionPadronResponse;

@Component
public class GeneracionDtoMapper {

    public FiltroGeneracion toFiltro(GeneracionFiltroRequest request) {
        return FiltroGeneracion.builder()
                .lectivoId(request.lectivoId())
                .facultadId(request.facultadId())
                .geograficaId(request.geograficaId())
                .claseChequeraId(request.claseChequeraId())
                .tipoChequeraId(request.tipoChequeraId())
                .soloNoEnviadas(Boolean.TRUE.equals(request.soloNoEnviadas()))
                .chequeraSerieIds(request.chequeraSerieIds())
                .forzarDeuda(Boolean.TRUE.equals(request.forzarDeuda()))
                .motivoOverride(request.motivoOverride())
                .borradorIds(request.borradorIds())
                .build();
    }

    public GeneracionJobResponse toResponse(GeneracionJob job) {
        return GeneracionJobResponse.builder()
                .jobId(job.getJobId())
                .etapa(job.getEtapa() == null ? null : job.getEtapa().name())
                .jobOrigenId(job.getJobOrigenId())
                .lectivoId(job.getLectivoId())
                .facultadId(job.getFacultadId())
                .geograficaId(job.getGeograficaId())
                .claseChequeraId(job.getClaseChequeraId())
                .tipoChequeraId(job.getTipoChequeraId())
                .soloNoEnviadas(job.isSoloNoEnviadas())
                .forzarDeuda(job.isForzarDeuda())
                .motivoOverride(job.getMotivoOverride())
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

    public GeneracionJobTareaResponse toResponse(GeneracionJobTarea tarea) {
        return GeneracionJobTareaResponse.builder()
                .tareaId(tarea.getTareaId())
                .chequeraId(tarea.getChequeraId())
                .personaId(tarea.getPersonaId())
                .documentoId(tarea.getDocumentoId())
                .facultadId(tarea.getFacultadId())
                .tipoChequeraId(tarea.getTipoChequeraId())
                .chequeraSerieId(tarea.getChequeraSerieId())
                .cursoId(tarea.getCursoId())
                .personaNombre(tarea.getPersonaNombre())
                .borradorId(tarea.getBorradorId())
                .estado(tarea.getEstado() == null ? null : tarea.getEstado().name())
                .codigo(tarea.getCodigo())
                .mensaje(tarea.getMensaje())
                .finalizado(tarea.getFinalizado())
                .build();
    }

    public GeneracionJobDetalleResponse toResponse(GeneracionJobDetalle detalle) {
        return GeneracionJobDetalleResponse.builder()
                .job(toResponse(detalle.getJob()))
                .tareas(detalle.getTareas().stream().map(this::toResponse).toList())
                .build();
    }

    public GeneracionPadronResponse toResponse(GeneracionPadronItem item) {
        return GeneracionPadronResponse.builder()
                .chequeraId(item.getChequeraId())
                .personaId(item.getPersonaId())
                .documentoId(item.getDocumentoId())
                .personaNombre(item.getPersonaNombre())
                .facultadId(item.getFacultadId())
                .tipoChequeraId(item.getTipoChequeraId())
                .tipoChequeraNombre(item.getTipoChequeraNombre())
                .claseChequeraId(item.getClaseChequeraId())
                .claseChequeraNombre(item.getClaseChequeraNombre())
                .chequeraSerieId(item.getChequeraSerieId())
                .cursoId(item.getCursoId())
                .arancelTipoId(item.getArancelTipoId())
                .arancelTipoNombre(item.getArancelTipoNombre())
                .alternativaId(item.getAlternativaId())
                .enviado(item.getEnviado())
                .borradorAbiertoId(item.getBorradorAbiertoId())
                .build();
    }
}
