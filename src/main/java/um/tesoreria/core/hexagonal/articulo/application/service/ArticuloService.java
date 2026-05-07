package um.tesoreria.core.hexagonal.articulo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.*;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;

import java.util.List;
import java.util.Optional;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.model.PaginatedResponse;


@Service
@RequiredArgsConstructor
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
        return createArticuloUseCase.createArticulo(articulo);
    }

    public Optional<Articulo> getArticuloById(Long id) {
        return getArticuloByIdUseCase.getArticuloById(id);
    }

    public List<Articulo> getAllArticulos() {
        return getAllArticulosUseCase.getAllArticulos();
    }

    public Optional<Articulo> updateArticulo(Long id, Articulo articulo) {
        return updateArticuloUseCase.updateArticulo(id, articulo);
    }

    public boolean deleteArticulo(Long id) {
        return deleteArticuloUseCase.deleteArticulo(id);
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
