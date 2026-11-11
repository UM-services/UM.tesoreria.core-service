package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;

public interface CreateSolicitudAccesoUseCase {
    SolicitudAcceso create(SolicitudAcceso solicitud);
}
