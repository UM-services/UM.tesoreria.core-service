package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteArticuloUseCaseImplTest {

    private ArticuloRepository repository;
    private DeleteArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ArticuloRepository.class);
        useCase = new DeleteArticuloUseCaseImpl(repository);
    }

    @Test
    void existente_seBloqueaYSeBorra() {
        when(repository.findByIdForUpdate(5L)).thenReturn(Optional.of(Articulo.builder().articuloId(5L).build()));

        useCase.deleteArticulo(5L);

        InOrder orden = inOrder(repository);
        orden.verify(repository).findByIdForUpdate(5L);
        orden.verify(repository).deleteById(5L);
    }

    @Test
    void inexistente_404SinBorrar() {
        when(repository.findByIdForUpdate(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.deleteArticulo(6L)).isInstanceOf(ArticuloException.class);
        verify(repository, never()).deleteById(any());
    }
}
