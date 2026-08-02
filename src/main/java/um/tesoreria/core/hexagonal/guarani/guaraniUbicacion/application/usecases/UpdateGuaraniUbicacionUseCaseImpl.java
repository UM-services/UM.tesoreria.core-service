package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.exception.GuaraniUbicacionException;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.UpdateGuaraniUbicacionUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class UpdateGuaraniUbicacionUseCaseImpl implements UpdateGuaraniUbicacionUseCase {
    private final GuaraniUbicacionRepository guaraniUbicacionRepository;

    @Override
    public GuaraniUbicacion update(GuaraniUbicacion guaraniUbicacion) {
        Integer id = guaraniUbicacion.getGuaraniUbicacionId();
        guaraniUbicacionRepository.findById(id)
                .orElseThrow(() -> new GuaraniUbicacionException(id));
        return guaraniUbicacionRepository.save(guaraniUbicacion);
    }
}
