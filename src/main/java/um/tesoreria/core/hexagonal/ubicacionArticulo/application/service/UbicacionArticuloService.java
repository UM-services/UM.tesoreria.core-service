package um.tesoreria.core.hexagonal.ubicacionArticulo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UbicacionArticuloService {
    private final GetAllUbicacionArticulosUseCase getAllUbicacionArticulosUseCase;
    private final SaveUbicacionArticuloUseCase saveUbicacionArticuloUseCase;
    private final GetUbicacionArticuloUseCase getUbicacionArticuloUseCase;
    private final GetUbicacionArticulosByArticuloUseCase getUbicacionArticulosByArticuloUseCase;

    public List<UbicacionArticulo> findAll() {
        return getAllUbicacionArticulosUseCase.getAll();
    }

    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        return saveUbicacionArticuloUseCase.save(ubicacionArticulo);
    }

    public List<UbicacionArticulo> findAllByArticuloId(Long articuloId) {
        return getUbicacionArticulosByArticuloUseCase.getByArticuloId(articuloId);
    }

    public Optional<UbicacionArticulo> getByUbicacionAndArticulo(Integer ubicacionId, Long articuloId) {
        return getUbicacionArticuloUseCase.getByUbicacionAndArticulo(ubicacionId, articuloId);
    }
}
