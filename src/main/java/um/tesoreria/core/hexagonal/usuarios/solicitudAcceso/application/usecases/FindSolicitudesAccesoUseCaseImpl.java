package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.in.FindSolicitudesAccesoUseCase;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.ports.out.SolicitudAccesoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindSolicitudesAccesoUseCaseImpl implements FindSolicitudesAccesoUseCase {

    private final SolicitudAccesoRepository repository;

    @Override
    public List<SolicitudAcceso> findPendientes() {
        return repository.findPendientes();
    }

    @Override
    public List<SolicitudAcceso> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}
