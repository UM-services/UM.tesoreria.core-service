package um.tesoreria.core.hexagonal.compras.articulo.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Los choques son simulados: los casos de uso son mocks que lanzan la excepción ya traducida. */
class ArticuloServiceTest {

    private CreateArticuloUseCase crear;
    private UpdateArticuloUseCase editar;
    private DeleteArticuloUseCase borrar;
    private ArticuloService service;
    private final Articulo cambios = Articulo.builder().nombre("N").build();

    @BeforeEach
    void setUp() {
        crear = mock(CreateArticuloUseCase.class);
        editar = mock(UpdateArticuloUseCase.class);
        borrar = mock(DeleteArticuloUseCase.class);
        service = new ArticuloService(crear, mock(GetArticuloByIdUseCase.class), mock(GetAllArticulosUseCase.class), editar, borrar,
                mock(GetNewArticuloUseCase.class), mock(GetPaginatedArticulosUseCase.class), mock(SearchArticulosUseCase.class));
    }

    @Test
    void interbloqueo_seReintentaUnaVez() {
        var guardado = Articulo.builder().articuloId(7L).build();
        when(editar.updateArticulo(7L, cambios)).thenThrow(ArticuloConflictException.bloqueado(7L, true)).thenReturn(guardado);

        assertThat(service.updateArticulo(7L, cambios)).isSameAs(guardado);
        verify(editar, times(2)).updateArticulo(7L, cambios);
    }

    @Test
    void segundoInterbloqueo_salePor409SinTercerIntento() {
        when(editar.updateArticulo(7L, cambios)).thenThrow(ArticuloConflictException.bloqueado(7L, true));

        assertThatThrownBy(() -> service.updateArticulo(7L, cambios)).isInstanceOf(ArticuloConflictException.class);
        verify(editar, times(2)).updateArticulo(7L, cambios);
    }

    @Test
    void esperaDeBloqueoVencida_noSeReintenta() {
        when(editar.updateArticulo(7L, cambios)).thenThrow(ArticuloConflictException.bloqueado(7L, false));

        assertThatThrownBy(() -> service.updateArticulo(7L, cambios))
                .isInstanceOfSatisfying(ArticuloConflictException.class, ex -> assertThat(ex.getMotivo()).isEqualTo(ArticuloConflictException.Motivo.BLOQUEADO));
        verify(editar, times(1)).updateArticulo(7L, cambios);
    }

    @Test
    void otrosConflictos_noSeReintentan() {
        var alta = Articulo.builder().articuloId(8L).build();
        when(crear.createArticulo(alta)).thenThrow(ArticuloConflictException.idDuplicado(8L));

        assertThatThrownBy(() -> service.createArticulo(alta)).isInstanceOf(ArticuloConflictException.class);
        verify(crear, times(1)).createArticulo(alta);
    }

    @Test
    void bajaYAlta_tambienSeReintentanAnteInterbloqueo() {
        doThrow(ArticuloConflictException.bloqueado(9L, true)).doNothing().when(borrar).deleteArticulo(9L);
        service.deleteArticulo(9L);
        verify(borrar, times(2)).deleteArticulo(9L);

        var alta = Articulo.builder().articuloId(10L).build();
        when(crear.createArticulo(alta)).thenThrow(ArticuloConflictException.bloqueado(10L, true)).thenReturn(alta);
        assertThat(service.createArticulo(alta)).isSameAs(alta);
        verify(crear, times(2)).createArticulo(alta);
    }
}
