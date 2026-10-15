package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraCotizacionTramoUseCasesTest {

    @Mock
    private CompraCotizacionTramoRepository repository;

    private CompraCotizacionTramo tramo(Long id, Integer tope, Integer cantidad) {
        return CompraCotizacionTramo.builder().cotizacionTramoId(id).topeMultiplico(tope).cantidadMinima(cantidad).build();
    }

    @Test
    void getAllDelega() {
        when(repository.findAll()).thenReturn(List.of(tramo(1L, 2, 1)));

        assertThat(new GetAllCotizacionTramosUseCaseImpl(repository).getAll()).hasSize(1);
    }

    @Test
    void getByIdDelega() {
        when(repository.findById(1L)).thenReturn(Optional.of(tramo(1L, 2, 1)));

        assertThat(new GetCotizacionTramoByIdUseCaseImpl(repository).getById(1L)).isPresent();
    }

    @Test
    void createExigeTramo() {
        assertThatThrownBy(() -> new CreateCotizacionTramoUseCaseImpl(repository).create(null))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void createValidaCantidad() {
        assertThatThrownBy(() -> new CreateCotizacionTramoUseCaseImpl(repository).create(tramo(null, 2, 0)))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void createValidaTope() {
        assertThatThrownBy(() -> new CreateCotizacionTramoUseCaseImpl(repository).create(tramo(null, 0, 1)))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void createRechazaTopeDuplicado() {
        when(repository.findAll()).thenReturn(List.of(tramo(1L, 2, 1)));

        assertThatThrownBy(() -> new CreateCotizacionTramoUseCaseImpl(repository).create(tramo(null, 2, 2)))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void createRechazaSegundoAbierto() {
        when(repository.findAll()).thenReturn(List.of(tramo(1L, null, 1)));

        assertThatThrownBy(() -> new CreateCotizacionTramoUseCaseImpl(repository).create(tramo(null, null, 3)))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void createGuarda() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any())).thenReturn(tramo(1L, 2, 1));

        assertThat(new CreateCotizacionTramoUseCaseImpl(repository).create(tramo(null, 2, 1))
                .getCotizacionTramoId()).isEqualTo(1L);
    }

    @Test
    void updateInexistenteDevuelveNull() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(new UpdateCotizacionTramoUseCaseImpl(repository).update(tramo(null, 2, 1), 99L)).isNull();
    }

    @Test
    void updateAplicaCamposYMueveTope() {
        CompraCotizacionTramo actual = tramo(1L, 2, 1);
        when(repository.findById(1L)).thenReturn(Optional.of(actual));
        when(repository.findAll()).thenReturn(List.of(actual));
        when(repository.save(actual)).thenReturn(actual);

        CompraCotizacionTramo resultado = new UpdateCotizacionTramoUseCaseImpl(repository).update(
                CompraCotizacionTramo.builder().nombre("Nuevo").topeMultiplico(6).cantidadMinima(2).activo((byte) 0).build(), 1L);

        assertThat(resultado.getNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getTopeMultiplico()).isEqualTo(6);
        assertThat(resultado.getCantidadMinima()).isEqualTo(2);
        assertThat(resultado.getActivo()).isEqualTo((byte) 0);
    }

    @Test
    void updatePermiteLimpiarTope() {
        CompraCotizacionTramo actual = tramo(1L, 2, 1);
        when(repository.findById(1L)).thenReturn(Optional.of(actual));
        when(repository.findAll()).thenReturn(List.of(actual));
        when(repository.save(actual)).thenReturn(actual);

        CompraCotizacionTramo resultado = new UpdateCotizacionTramoUseCaseImpl(repository)
                .update(CompraCotizacionTramo.builder().nombre("Abierto").topeMultiplico(null).build(), 1L);

        assertThat(resultado.getTopeMultiplico()).isNull();
        assertThat(resultado.getCantidadMinima()).isEqualTo(1);
    }

    @Test
    void updateRechazaConflictoDeTope() {
        when(repository.findById(1L)).thenReturn(Optional.of(tramo(1L, 2, 1)));
        when(repository.findAll()).thenReturn(List.of(tramo(1L, 2, 1), tramo(2L, 6, 2)));

        assertThatThrownBy(() -> new UpdateCotizacionTramoUseCaseImpl(repository)
                .update(tramo(null, 6, 3), 1L))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void updateValidaCampos() {
        assertThatThrownBy(() -> new UpdateCotizacionTramoUseCaseImpl(repository).update(tramo(null, 2, 0), 1L))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void deleteExigePerfilExistente() {
        assertThatThrownBy(() -> new DeleteCotizacionTramoUseCaseImpl(repository).delete(99L))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void deleteExigeId() {
        assertThatThrownBy(() -> new DeleteCotizacionTramoUseCaseImpl(repository).delete(null))
                .isInstanceOf(CompraCotizacionTramoException.class);
    }

    @Test
    void deleteDelega() {
        when(repository.existsById(1L)).thenReturn(true);

        new DeleteCotizacionTramoUseCaseImpl(repository).delete(1L);

        verify(repository).deleteById(1L);
    }

}
