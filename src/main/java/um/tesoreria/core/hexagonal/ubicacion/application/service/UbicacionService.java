package um.tesoreria.core.hexagonal.ubicacion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.ubicacion.domain.ports.in.GetAllUbicacionesUseCase;
import um.tesoreria.core.hexagonal.ubicacion.domain.ports.in.GetUbicacionesBySedeUseCase;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionService {
    private final GetAllUbicacionesUseCase getAllUbicacionesUseCase;
    private final GetUbicacionesBySedeUseCase getUbicacionesBySedeUseCase;

    public List<Ubicacion> findAll() {
        return getAllUbicacionesUseCase.getAllUbicaciones();
    }

    public List<Ubicacion> findAllBySede(Integer geograficaId) {
        return getUbicacionesBySedeUseCase.getUbicacionesBySede(geograficaId);
    }
}
