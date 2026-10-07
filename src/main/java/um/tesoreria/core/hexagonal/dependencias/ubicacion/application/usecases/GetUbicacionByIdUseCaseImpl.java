package um.tesoreria.core.hexagonal.dependencias.ubicacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.in.GetUbicacionByIdUseCase;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.out.UbicacionRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetUbicacionByIdUseCaseImpl implements GetUbicacionByIdUseCase {
    private final UbicacionRepository repository;

    @Override
    public Optional<Ubicacion> getUbicacionById(Integer ubicacionId) {
        return repository.findById(ubicacionId);
    }
}
