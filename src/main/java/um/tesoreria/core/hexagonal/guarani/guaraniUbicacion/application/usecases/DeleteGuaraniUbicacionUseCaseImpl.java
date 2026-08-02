package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.exception.GuaraniUbicacionException;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.DeleteGuaraniUbicacionUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class DeleteGuaraniUbicacionUseCaseImpl implements DeleteGuaraniUbicacionUseCase {
    private final GuaraniUbicacionRepository guaraniUbicacionRepository;

    @Override
    public void delete(Integer id) {
        guaraniUbicacionRepository.findById(id)
                .orElseThrow(() -> new GuaraniUbicacionException(id));
        guaraniUbicacionRepository.deleteById(id);
    }
}
