package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.usecases;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.GetGuaraniUbicacionByIdUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class GetGuaraniUbicacionByIdUseCaseImpl implements GetGuaraniUbicacionByIdUseCase {
    private final GuaraniUbicacionRepository guaraniUbicacionRepository;

    @Override
    public Optional<GuaraniUbicacion> getById(Integer id) {
        return guaraniUbicacionRepository.findById(id);
    }
}
