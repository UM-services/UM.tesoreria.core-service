package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.entity.SolicitudAccesoEntity;

@Component
public class SolicitudAccesoMapper {

    public SolicitudAcceso toDomain(SolicitudAccesoEntity entity) {
        if (entity == null) return null;
        SolicitudAcceso.SolicitudAccesoBuilder builder = SolicitudAcceso.builder()
                .solicitudAccesoId(entity.getSolicitudAccesoId())
                .userId(entity.getUserId())
                .clave(entity.getClave())
                .modulo(entity.getModulo())
                .comentario(entity.getComentario())
                .observacion(entity.getObservacion())
                .resueltoPor(entity.getResueltoPor())
                .resuelto(entity.getResuelto())
                .created(entity.getCreated())
                .updated(entity.getUpdated());
        if (entity.getAplicacion() != null) builder.aplicacion(entity.getAplicacion());
        if (entity.getEstado() != null) builder.estado(entity.getEstado());
        return builder.build();
    }

    public SolicitudAccesoEntity toEntity(SolicitudAcceso domain) {
        if (domain == null) return null;
        SolicitudAccesoEntity.SolicitudAccesoEntityBuilder builder =
                SolicitudAccesoEntity.builder()
                        .solicitudAccesoId(domain.getSolicitudAccesoId())
                        .userId(domain.getUserId())
                        .clave(domain.getClave())
                        .modulo(domain.getModulo())
                        .comentario(domain.getComentario())
                        .observacion(domain.getObservacion())
                        .resueltoPor(domain.getResueltoPor())
                        .resuelto(domain.getResuelto());
        if (domain.getAplicacion() != null) builder.aplicacion(domain.getAplicacion());
        if (domain.getEstado() != null) builder.estado(domain.getEstado());
        return builder.build();
    }
}
