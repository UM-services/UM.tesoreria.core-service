package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.IncompletasJobEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.IncompletasJobTareaEntity;

@Component
public class IncompletasJobMapper {

    public IncompletasJobEntity toEntity(IncompletasJob job) {
        return IncompletasJobEntity.builder()
                .jobId(job.getJobId())
                .etapa(job.getEtapa())
                .jobOrigenId(job.getJobOrigenId())
                .lectivoId(job.getLectivoId())
                .facultadId(job.getFacultadId())
                .geograficaId(job.getGeograficaId())
                .claseChequeraId(job.getClaseChequeraId())
                .soloNoEnviadas(job.isSoloNoEnviadas())
                .estado(job.getEstado())
                .totalTareas(job.getTotalTareas())
                .tareasOk(job.getTareasOk())
                .tareasOmitidas(job.getTareasOmitidas())
                .tareasError(job.getTareasError())
                .tareasPendientes(job.getTareasPendientes())
                .usuarioId(job.getUsuarioId())
                .usuarioLogin(job.getUsuarioLogin())
                .heartbeat(job.getHeartbeat())
                .iniciado(job.getIniciado())
                .finalizado(job.getFinalizado())
                .mensaje(job.getMensaje())
                .ultimaChequeraId(job.getUltimaChequeraId())
                .build();
    }

    public IncompletasJob toDomain(IncompletasJobEntity entity) {
        return IncompletasJob.builder()
                .jobId(entity.getJobId())
                .etapa(entity.getEtapa())
                .jobOrigenId(entity.getJobOrigenId())
                .lectivoId(entity.getLectivoId())
                .facultadId(entity.getFacultadId())
                .geograficaId(entity.getGeograficaId())
                .claseChequeraId(entity.getClaseChequeraId())
                .soloNoEnviadas(entity.isSoloNoEnviadas())
                .estado(entity.getEstado())
                .totalTareas(entity.getTotalTareas())
                .tareasOk(entity.getTareasOk())
                .tareasOmitidas(entity.getTareasOmitidas())
                .tareasError(entity.getTareasError())
                .tareasPendientes(entity.getTareasPendientes())
                .usuarioId(entity.getUsuarioId())
                .usuarioLogin(entity.getUsuarioLogin())
                .heartbeat(entity.getHeartbeat())
                .iniciado(entity.getIniciado())
                .finalizado(entity.getFinalizado())
                .mensaje(entity.getMensaje())
                .ultimaChequeraId(entity.getUltimaChequeraId())
                .build();
    }

    public IncompletasJobTareaEntity toEntity(IncompletasJobTarea tarea) {
        return IncompletasJobTareaEntity.builder()
                .tareaId(tarea.getTareaId())
                .jobId(tarea.getJobId())
                .chequeraId(tarea.getChequeraId())
                .personaId(tarea.getPersonaId())
                .documentoId(tarea.getDocumentoId())
                .facultadId(tarea.getFacultadId())
                .tipoChequeraId(tarea.getTipoChequeraId())
                .chequeraSerieId(tarea.getChequeraSerieId())
                .personaNombre(tarea.getPersonaNombre())
                .borradorId(tarea.getBorradorId())
                .estado(tarea.getEstado())
                .codigo(tarea.getCodigo())
                .mensaje(tarea.getMensaje())
                .finalizado(tarea.getFinalizado())
                .build();
    }

    public IncompletasJobTarea toDomain(IncompletasJobTareaEntity entity) {
        return IncompletasJobTarea.builder()
                .tareaId(entity.getTareaId())
                .jobId(entity.getJobId())
                .chequeraId(entity.getChequeraId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .personaNombre(entity.getPersonaNombre())
                .borradorId(entity.getBorradorId())
                .estado(entity.getEstado())
                .codigo(entity.getCodigo())
                .mensaje(entity.getMensaje())
                .finalizado(entity.getFinalizado())
                .build();
    }
}
