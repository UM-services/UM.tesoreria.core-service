package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.GeneracionJobEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.GeneracionJobTareaEntity;

@Component
public class GeneracionJobMapper {

    public GeneracionJobEntity toEntity(GeneracionJob job) {
        return GeneracionJobEntity.builder()
                .jobId(job.getJobId())
                .etapa(job.getEtapa())
                .jobOrigenId(job.getJobOrigenId())
                .lectivoId(job.getLectivoId())
                .facultadId(job.getFacultadId())
                .geograficaId(job.getGeograficaId())
                .claseChequeraId(job.getClaseChequeraId())
                .tipoChequeraId(job.getTipoChequeraId())
                .soloNoEnviadas(job.isSoloNoEnviadas())
                .forzarDeuda(job.isForzarDeuda())
                .motivoOverride(job.getMotivoOverride())
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

    public GeneracionJob toDomain(GeneracionJobEntity entity) {
        return GeneracionJob.builder()
                .jobId(entity.getJobId())
                .etapa(entity.getEtapa())
                .jobOrigenId(entity.getJobOrigenId())
                .lectivoId(entity.getLectivoId())
                .facultadId(entity.getFacultadId())
                .geograficaId(entity.getGeograficaId())
                .claseChequeraId(entity.getClaseChequeraId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .soloNoEnviadas(entity.isSoloNoEnviadas())
                .forzarDeuda(entity.isForzarDeuda())
                .motivoOverride(entity.getMotivoOverride())
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

    public GeneracionJobTareaEntity toEntity(GeneracionJobTarea tarea) {
        return GeneracionJobTareaEntity.builder()
                .tareaId(tarea.getTareaId())
                .jobId(tarea.getJobId())
                .chequeraId(tarea.getChequeraId())
                .personaId(tarea.getPersonaId())
                .documentoId(tarea.getDocumentoId())
                .facultadId(tarea.getFacultadId())
                .tipoChequeraId(tarea.getTipoChequeraId())
                .chequeraSerieId(tarea.getChequeraSerieId())
                .cursoId(tarea.getCursoId())
                .personaNombre(tarea.getPersonaNombre())
                .borradorId(tarea.getBorradorId())
                .estado(tarea.getEstado())
                .codigo(tarea.getCodigo())
                .mensaje(tarea.getMensaje())
                .finalizado(tarea.getFinalizado())
                .build();
    }

    public GeneracionJobTarea toDomain(GeneracionJobTareaEntity entity) {
        return GeneracionJobTarea.builder()
                .tareaId(entity.getTareaId())
                .jobId(entity.getJobId())
                .chequeraId(entity.getChequeraId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .cursoId(entity.getCursoId())
                .personaNombre(entity.getPersonaNombre())
                .borradorId(entity.getBorradorId())
                .estado(entity.getEstado())
                .codigo(entity.getCodigo())
                .mensaje(entity.getMensaje())
                .finalizado(entity.getFinalizado())
                .build();
    }
}
