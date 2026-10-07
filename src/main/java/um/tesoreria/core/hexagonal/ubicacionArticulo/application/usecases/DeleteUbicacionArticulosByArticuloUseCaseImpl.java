package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.DeleteUbicacionArticulosByArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

import java.util.List;

/**
 * Solo dentro de la transacción de la baja del artículo, que ya tiene el artículo bloqueado: el orden de bloqueo
 * es siempre artículo y después vínculos. Sin esa transacción, borrar vínculos sueltos no tiene sentido.
 */
@Component
@RequiredArgsConstructor
public class DeleteUbicacionArticulosByArticuloUseCaseImpl implements DeleteUbicacionArticulosByArticuloUseCase {
    private final UbicacionArticuloRepository repository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<UbicacionArticulo> deleteByArticuloId(Long articuloId) {
        return repository.deleteAllByArticuloId(articuloId);
    }
}
