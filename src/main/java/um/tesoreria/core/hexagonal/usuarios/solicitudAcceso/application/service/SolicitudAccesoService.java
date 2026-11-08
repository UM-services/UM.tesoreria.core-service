package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in.CreateSolicitudAccesoUseCase;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in.FindSolicitudesAccesoUseCase;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in.ResolveSolicitudAccesoUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudAccesoService {

    private final CreateSolicitudAccesoUseCase createSolicitudAccesoUseCase;
    private final FindSolicitudesAccesoUseCase findSolicitudesAccesoUseCase;
    private final ResolveSolicitudAccesoUseCase resolveSolicitudAccesoUseCase;

    public SolicitudAcceso create(SolicitudAcceso solicitud) {
        return createSolicitudAccesoUseCase.create(solicitud);
    }

    public List<SolicitudAcceso> findPendientes() {
        return findSolicitudesAccesoUseCase.findPendientes();
    }

    public List<SolicitudAcceso> findByUserId(Long userId) {
        return findSolicitudesAccesoUseCase.findByUserId(userId);
    }

    public SolicitudAcceso resolve(
            Long solicitudAccesoId, String estado, String observacion, Long resueltoPor) {
        return resolveSolicitudAccesoUseCase.resolve(
                solicitudAccesoId, estado, observacion, resueltoPor);
    }
}
