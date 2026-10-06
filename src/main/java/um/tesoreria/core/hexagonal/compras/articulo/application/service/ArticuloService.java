package um.tesoreria.core.hexagonal.compras.articulo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.model.PaginatedResponse;


/**
 * Sin transacción propia: cada escritura corre en la de su caso de uso, así un reintento abre una nueva.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticuloService {

    private final CreateArticuloUseCase createArticuloUseCase;
    private final GetArticuloByIdUseCase getArticuloByIdUseCase;
    private final GetAllArticulosUseCase getAllArticulosUseCase;
    private final UpdateArticuloUseCase updateArticuloUseCase;
    private final DeleteArticuloUseCase deleteArticuloUseCase;
    private final GetNewArticuloUseCase getNewArticuloUseCase;
    private final GetPaginatedArticulosUseCase getPaginatedArticulosUseCase;
    private final SearchArticulosUseCase searchArticulosUseCase;

    public Articulo createArticulo(Articulo articulo) {
        return conUnReintento("alta", articulo.getArticuloId(), () -> createArticuloUseCase.createArticulo(articulo));
    }

    public Optional<Articulo> getArticuloById(Long id) {
        return getArticuloByIdUseCase.getArticuloById(id);
    }

    public List<Articulo> getAllArticulos() {
        return getAllArticulosUseCase.getAllArticulos();
    }

    public Articulo updateArticulo(Long id, Articulo cambios) {
        return conUnReintento("edición", id, () -> updateArticuloUseCase.updateArticulo(id, cambios));
    }

    public void deleteArticulo(Long id) {
        conUnReintento("baja", id, () -> {
            deleteArticuloUseCase.deleteArticulo(id);
            return null;
        });
    }

    /**
     * Ante un interbloqueo MySQL deshace la transacción entera, así que repetir la escritura en otra es seguro;
     * se reintenta una sola vez y un segundo choque sale como 409.
     */
    private <T> T conUnReintento(String operacion, Long articuloId, Supplier<T> escritura) {
        try {
            return escritura.get();
        } catch (ArticuloConflictException ex) {
            if (!ex.isReintentable()) {
                throw ex;
            }
            log.warn("Artículo {} ({}): {}; se reintenta una vez", articuloId, operacion, ex.getMessage());
            return escritura.get();
        }
    }

    public Articulo getNewArticulo() {
        return getNewArticuloUseCase.getNewArticulo();
    }

        public List<ArticuloSearch> searchArticulos(List<String> conditions) {
        return searchArticulosUseCase.searchArticulos(conditions);
    }

    public PaginatedResponse<Articulo> getPaginatedArticulosByTipo(String tipo, int page, int size) {
        return getPaginatedArticulosUseCase.getPaginatedArticulosByTipo(tipo, page, size);
    }

}
