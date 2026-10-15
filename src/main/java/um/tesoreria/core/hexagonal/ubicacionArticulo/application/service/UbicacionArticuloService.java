package um.tesoreria.core.hexagonal.ubicacionArticulo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.*;
import java.util.List;
import java.util.Optional;

/**
 * Sin transacción propia: cada llamada a un caso de uso abre la suya.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UbicacionArticuloService {
    private final GetAllUbicacionArticulosUseCase getAllUbicacionArticulosUseCase;
    private final SaveUbicacionArticuloUseCase saveUbicacionArticuloUseCase;
    private final GetUbicacionArticuloUseCase getUbicacionArticuloUseCase;
    private final GetUbicacionArticulosByArticuloUseCase getUbicacionArticulosByArticuloUseCase;

    public List<UbicacionArticulo> findAll() {
        return getAllUbicacionArticulosUseCase.getAll();
    }

    /**
     * Asignación idempotente por par (ubicación, artículo). Si otra transacción insertó el mismo par al mismo
     * tiempo (o hubo un interbloqueo), se reintenta una sola vez en una transacción nueva, que ya ve la fila
     * y la actualiza. Un segundo choque sale como 409.
     */
    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        try {
            return saveUbicacionArticuloUseCase.save(ubicacionArticulo);
        } catch (UbicacionArticuloConflictException ex) {
            if (!ex.isReintentable()) {
                throw ex;
            }
            log.warn("Asignación ubicación {} artículo {}: {}; se reintenta una vez",
                    ubicacionArticulo.getUbicacionId(), ubicacionArticulo.getArticuloId(), ex.getMessage());
            return saveUbicacionArticuloUseCase.save(ubicacionArticulo);
        }
    }

    public List<UbicacionArticulo> findAllByArticuloId(Long articuloId) {
        return getUbicacionArticulosByArticuloUseCase.getByArticuloId(articuloId);
    }

    public Optional<UbicacionArticulo> getByUbicacionAndArticulo(Integer ubicacionId, Long articuloId) {
        return getUbicacionArticuloUseCase.getByUbicacionAndArticulo(ubicacionId, articuloId);
    }
}
