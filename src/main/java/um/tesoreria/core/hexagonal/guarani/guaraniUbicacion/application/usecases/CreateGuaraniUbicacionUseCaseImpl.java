package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.CreateGuaraniUbicacionUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class CreateGuaraniUbicacionUseCaseImpl implements CreateGuaraniUbicacionUseCase {
    private final GuaraniUbicacionRepository guaraniUbicacionRepository;

    @Override
    public GuaraniUbicacion create(GuaraniUbicacion guaraniUbicacion) {
        return guaraniUbicacionRepository.save(guaraniUbicacion);
    }
}
