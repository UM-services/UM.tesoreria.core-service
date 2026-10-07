package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.UpdateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

@Component
@RequiredArgsConstructor
public class UpdateArticuloUseCaseImpl implements UpdateArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    @Transactional
    public Articulo updateArticulo(Long id, Articulo cambios) {
        ArticuloReglas.validarCambios(cambios);
        ArticuloReglas.normalizarNumeros(cambios);
        // Bloqueada antes de leer el estado actual: dos ediciones del core se serializan
        Articulo actual = repository.findByIdForUpdate(id).orElseThrow(() -> new ArticuloException(id));
        return repository.update(actual.conCambios(cambios));
    }
}
