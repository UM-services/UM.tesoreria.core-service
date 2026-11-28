package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioJobEntity;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioJobTareaEntity;

@Component
public class EnvioJobMapper {

    public EnvioJobEntity toEntity(EnvioJob job) {
        return EnvioJobEntity.builder()
                .jobId(job.getJobId())
                .operacion(job.getOperacion())
                .lectivoId(job.getLectivoId())
                .facultadId(job.getFacultadId())
                .geograficaId(job.getGeograficaId())
                .soloNoEnviadas(job.isSoloNoEnviadas())
                .codigoBarras(job.isCodigoBarras())
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

    public EnvioJob toDomain(EnvioJobEntity entity) {
        return EnvioJob.builder()
                .jobId(entity.getJobId())
                .operacion(entity.getOperacion())
                .lectivoId(entity.getLectivoId())
                .facultadId(entity.getFacultadId())
                .geograficaId(entity.getGeograficaId())
                .soloNoEnviadas(entity.isSoloNoEnviadas())
                .codigoBarras(entity.isCodigoBarras())
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

    public EnvioJobTareaEntity toEntity(EnvioJobTarea tarea) {
        return EnvioJobTareaEntity.builder()
                .tareaId(tarea.getTareaId())
                .jobId(tarea.getJobId())
                .chequeraId(tarea.getChequeraId())
                .personaId(tarea.getPersonaId())
                .documentoId(tarea.getDocumentoId())
                .facultadId(tarea.getFacultadId())
                .tipoChequeraId(tarea.getTipoChequeraId())
                .chequeraSerieId(tarea.getChequeraSerieId())
                .personaNombre(tarea.getPersonaNombre())
                .estado(tarea.getEstado())
                .codigo(tarea.getCodigo())
                .mensaje(tarea.getMensaje())
                .detalle(tarea.getDetalle())
                .retenida(tarea.getRetenida())
                .enviado(tarea.getEnviado())
                .finalizado(tarea.getFinalizado())
                .build();
    }

    public EnvioJobTarea toDomain(EnvioJobTareaEntity entity) {
        return EnvioJobTarea.builder()
                .tareaId(entity.getTareaId())
                .jobId(entity.getJobId())
                .chequeraId(entity.getChequeraId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .personaNombre(entity.getPersonaNombre())
                .estado(entity.getEstado())
                .codigo(entity.getCodigo())
                .mensaje(entity.getMensaje())
                .detalle(entity.getDetalle())
                .retenida(entity.getRetenida())
                .enviado(entity.getEnviado())
                .finalizado(entity.getFinalizado())
                .build();
    }
}
