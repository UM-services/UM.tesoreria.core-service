package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;

public interface ResolveSolicitudAccesoUseCase {
    SolicitudAcceso resolve(Long solicitudAccesoId, String estado, String observacion, Long resueltoPor);
}
