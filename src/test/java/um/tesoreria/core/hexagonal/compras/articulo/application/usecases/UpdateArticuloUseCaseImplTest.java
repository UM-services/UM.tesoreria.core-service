package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UpdateArticuloUseCaseImplTest {

    private ArticuloRepository repository;
    private UpdateArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ArticuloRepository.class);
        useCase = new UpdateArticuloUseCaseImpl(repository);
        when(repository.update(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void camposNulos_conservanLosActuales_ySeBloqueaAntesDeEscribir() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        var guardado = useCase.updateArticulo(7L, Articulo.builder().nombre("Nuevo").build());

        assertThat(guardado.getNombre()).isEqualTo("Nuevo");
        assertThat(guardado.getTipo()).isEqualTo("gasto");
        assertThat(guardado.getHabilitado()).isEqualTo((byte) 1);
        assertThat(guardado.getNumeroCuenta()).isEqualByComparingTo("51010101");
        InOrder orden = inOrder(repository);
        orden.verify(repository).findByIdForUpdate(7L);
        orden.verify(repository).update(any());
    }

    @Test
    void ceroExplicito_seAplica() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        useCase.updateArticulo(7L, Articulo.builder().habilitado((byte) 0).directo((byte) 0).build());

        var captor = ArgumentCaptor.forClass(Articulo.class);
        verify(repository).update(captor.capture());
        assertThat(captor.getValue().getHabilitado()).isZero();
        assertThat(captor.getValue().getDirecto()).isZero();
        assertThat(captor.getValue().getArticuloId()).isEqualTo(7L);
    }

    @Test
    void cambiosNumericos_seNormalizanAntesDeGuardar() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        var guardado = useCase.updateArticulo(7L, Articulo.builder().precio(new BigDecimal("0e-1000000000")).build());

        assertThat(guardado.getPrecio().scale()).isEqualTo(2);
    }

    @Test
    void idDelCuerpo_noCambiaElDeLaRuta() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        assertThat(useCase.updateArticulo(7L, Articulo.builder().articuloId(99L).build()).getArticuloId()).isEqualTo(7L);
    }

    @Test
    void inexistente_404SinEscribir() {
        when(repository.findByIdForUpdate(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateArticulo(8L, Articulo.builder().nombre("X").build()))
                .isInstanceOf(ArticuloException.class);
        verify(repository, never()).update(any());
    }

    @Test
    void tipoInvalido_400AntesDeLeer() {
        assertThatThrownBy(() -> useCase.updateArticulo(7L, Articulo.builder().tipo("").build()))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("tipo"));
        verifyNoInteractions(repository);
    }

    @Test
    void habilitadoFueraDeRango_400() {
        assertThatThrownBy(() -> useCase.updateArticulo(7L, Articulo.builder().habilitado((byte) 3).build()))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("habilitado"));
    }

    private static Articulo actual() {
        return Articulo.builder().articuloId(7L).nombre("Viejo").descripcion("D").unidad("U").precio(BigDecimal.TEN)
                .inventariable((byte) 0).stockMinimo(0L).numeroCuenta(new BigDecimal("51010101")).tipo("gasto")
                .directo((byte) 1).habilitado((byte) 1).build();
    }
}
