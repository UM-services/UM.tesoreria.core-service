package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.exception.SolicitudAccesoException;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.EstadoSolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in.ResolveSolicitudAccesoUseCase;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.out.SolicitudAccesoRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ResolveSolicitudAccesoUseCaseImpl implements ResolveSolicitudAccesoUseCase {

    private final SolicitudAccesoRepository repository;

    @Override
    public SolicitudAcceso resolve(
            Long solicitudAccesoId, String estado, String observacion, Long resueltoPor) {
        String destino = normalizarEstado(estado);
        if (destino == null) {
            throw new SolicitudAccesoException(
                    "El estado debe ser " + EstadoSolicitudAcceso.APROBADA.name() + " o "
                            + EstadoSolicitudAcceso.RECHAZADA.name());
        }

        SolicitudAcceso solicitud = repository.findById(solicitudAccesoId)
                .orElseThrow(() -> new SolicitudAccesoException(
                        "Solicitud de acceso no encontrada: " + solicitudAccesoId));

        if (!EstadoSolicitudAcceso.PENDIENTE.name().equals(solicitud.getEstado())) {
            throw new SolicitudAccesoException("La solicitud ya fue resuelta");
        }

        solicitud.setEstado(destino);
        solicitud.setObservacion(observacion);
        solicitud.setResueltoPor(resueltoPor);
        solicitud.setResuelto(LocalDateTime.now());
        return repository.save(solicitud);
    }

    private String normalizarEstado(String estado) {
        if (estado == null) {
            return null;
        }
        try {
            EstadoSolicitudAcceso valor = EstadoSolicitudAcceso.valueOf(estado.trim().toUpperCase());
            return valor == EstadoSolicitudAcceso.PENDIENTE ? null : valor.name();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
