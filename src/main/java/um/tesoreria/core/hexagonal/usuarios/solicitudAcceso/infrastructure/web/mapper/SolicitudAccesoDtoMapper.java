package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto.SolicitudAccesoRequest;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto.SolicitudAccesoResponse;

@Component
public class SolicitudAccesoDtoMapper {

    public SolicitudAcceso toDomain(SolicitudAccesoRequest request) {
        if (request == null) return null;
        return SolicitudAcceso.builder()
                .userId(request.getUserId())
                .aplicacion(request.getAplicacion())
                .clave(request.getClave())
                .modulo(request.getModulo())
                .comentario(request.getComentario())
                .build();
    }

    public SolicitudAccesoResponse toResponse(SolicitudAcceso domain) {
        if (domain == null) return null;
        return SolicitudAccesoResponse.builder()
                .solicitudAccesoId(domain.getSolicitudAccesoId())
                .userId(domain.getUserId())
                .aplicacion(domain.getAplicacion())
                .clave(domain.getClave())
                .modulo(domain.getModulo())
                .comentario(domain.getComentario())
                .estado(domain.getEstado())
                .observacion(domain.getObservacion())
                .resueltoPor(domain.getResueltoPor())
                .resuelto(domain.getResuelto())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }
}
