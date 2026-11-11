package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.exception.SolicitudAccesoException;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.EstadoSolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in.CreateSolicitudAccesoUseCase;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.out.SolicitudAccesoRepository;

@Component
@RequiredArgsConstructor
public class CreateSolicitudAccesoUseCaseImpl implements CreateSolicitudAccesoUseCase {

    private final SolicitudAccesoRepository repository;

    @Override
    public SolicitudAcceso create(SolicitudAcceso solicitud) {
        if (solicitud == null || solicitud.getUserId() == null) {
            throw new SolicitudAccesoException("El usuario que solicita el acceso es requerido");
        }

        if (solicitud.getAplicacion() == null || solicitud.getAplicacion().isBlank()) {
            solicitud.setAplicacion("TESORERIA");
        }
        solicitud.setEstado(EstadoSolicitudAcceso.PENDIENTE.name());
        solicitud.setResuelto(null);
        solicitud.setResueltoPor(null);
        solicitud.setObservacion(null);

        if (solicitud.getClave() != null
                && !solicitud.getClave().isBlank()
                && repository.existsPendienteByUserIdAndClave(
                        solicitud.getUserId(), solicitud.getClave())) {
            throw new SolicitudAccesoException(
                    "Ya existe una solicitud pendiente para el permiso " + solicitud.getClave());
        }

        return repository.save(solicitud);
    }
}
