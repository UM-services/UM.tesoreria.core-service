package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

/**
 * Una asignación por transacción. El reintento ante un choque vive en {@code UbicacionArticuloService}
 * (otro bean): una transacción que falló queda marcada para rollback y no se puede reutilizar.
 */
@Component
@RequiredArgsConstructor
public class SaveUbicacionArticuloUseCaseImpl implements SaveUbicacionArticuloUseCase {
    private final UbicacionArticuloRepository repository;
    @Override
    @Transactional
    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        if (ubicacionArticulo.getUbicacionId() == null) {
            throw new UbicacionArticuloValidationException("ubicacionId", "ubicacionId es obligatorio.");
        }
        if (ubicacionArticulo.getArticuloId() == null) {
            throw new UbicacionArticuloValidationException("articuloId", "articuloId es obligatorio.");
        }
        return repository.save(ubicacionArticulo);
    }
}
