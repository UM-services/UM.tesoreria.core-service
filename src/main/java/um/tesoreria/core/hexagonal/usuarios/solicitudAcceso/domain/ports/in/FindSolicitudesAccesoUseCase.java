package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;

import java.util.List;

public interface FindSolicitudesAccesoUseCase {
    List<SolicitudAcceso> findPendientes();

    List<SolicitudAcceso> findByUserId(Long userId);
}
