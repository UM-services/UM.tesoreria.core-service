package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioSolicitudEntity;

@Component
public class EnvioSolicitudMapper {

    public EnvioSolicitudEntity toEntity(EnvioSolicitud solicitud) {
        return EnvioSolicitudEntity.builder()
                .solicitudId(solicitud.getSolicitudId())
                .chequeraId(solicitud.getChequeraId())
                .facultadId(solicitud.getFacultadId())
                .tipoChequeraId(solicitud.getTipoChequeraId())
                .chequeraSerieId(solicitud.getChequeraSerieId())
                .lectivoId(solicitud.getLectivoId())
                .jobId(solicitud.getJobId())
                .origen(solicitud.getOrigen())
                .dedupeKey(solicitud.getDedupeKey())
                .estado(solicitud.getEstado())
                .intentos(solicitud.getIntentos())
                .lockHasta(solicitud.getLockHasta())
                .enviadoAt(solicitud.getEnviadoAt())
                .ultimoError(solicitud.getUltimoError())
                .build();
    }

    public EnvioSolicitud toDomain(EnvioSolicitudEntity entity) {
        return EnvioSolicitud.builder()
                .solicitudId(entity.getSolicitudId())
                .chequeraId(entity.getChequeraId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .lectivoId(entity.getLectivoId())
                .jobId(entity.getJobId())
                .origen(entity.getOrigen())
                .dedupeKey(entity.getDedupeKey())
                .estado(entity.getEstado())
                .intentos(entity.getIntentos())
                .lockHasta(entity.getLockHasta())
                .enviadoAt(entity.getEnviadoAt())
                .ultimoError(entity.getUltimoError())
                .build();
    }

}
