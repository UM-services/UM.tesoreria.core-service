package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.usecases;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.GetGuaraniUbicacionByUbicacionUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out.GuaraniUbicacionRepository;

@Component
@RequiredArgsConstructor
public class GetGuaraniUbicacionByUbicacionUseCaseImpl implements GetGuaraniUbicacionByUbicacionUseCase {
    private final GuaraniUbicacionRepository repository;

    @Override
    public Optional<GuaraniUbicacion> getByUbicacion(Integer ubicacion) {
        return repository.findByUbicacion(ubicacion);
    }
}
