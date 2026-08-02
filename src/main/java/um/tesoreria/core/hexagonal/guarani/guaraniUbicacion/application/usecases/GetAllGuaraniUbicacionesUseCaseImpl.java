package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.usecases;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.GetAllGuaraniUbicacionesUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class GetAllGuaraniUbicacionesUseCaseImpl implements GetAllGuaraniUbicacionesUseCase {
    private final GuaraniUbicacionRepository guaraniUbicacionRepository;

    @Override
    public List<GuaraniUbicacion> getAll() {
        return guaraniUbicacionRepository.findAll();
    }
}
