package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.exception.GuaraniUbicacionException;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.CreateGuaraniUbicacionUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.DeleteGuaraniUbicacionUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.GetAllGuaraniUbicacionesUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.GetGuaraniUbicacionByIdUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.UpdateGuaraniUbicacionUseCase;

@Service
@RequiredArgsConstructor
public class GuaraniUbicacionService {
    private final GetAllGuaraniUbicacionesUseCase getAllUseCase;
    private final GetGuaraniUbicacionByIdUseCase getByIdUseCase;
    private final CreateGuaraniUbicacionUseCase createUseCase;
    private final UpdateGuaraniUbicacionUseCase updateUseCase;
    private final DeleteGuaraniUbicacionUseCase deleteUseCase;

    public List<GuaraniUbicacion> findAll() {
        return getAllUseCase.getAll();
    }

    public GuaraniUbicacion findById(Integer id) {
        return getByIdUseCase.getById(id)
                .orElseThrow(() -> new GuaraniUbicacionException(id));
    }

    public GuaraniUbicacion create(GuaraniUbicacion guaraniUbicacion) {
        return createUseCase.create(guaraniUbicacion);
    }

    public GuaraniUbicacion update(GuaraniUbicacion guaraniUbicacion) {
        return updateUseCase.update(guaraniUbicacion);
    }

    public void delete(Integer id) {
        deleteUseCase.delete(id);
    }
}
