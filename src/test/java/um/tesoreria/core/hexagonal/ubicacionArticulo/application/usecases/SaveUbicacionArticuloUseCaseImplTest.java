package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SaveUbicacionArticuloUseCaseImplTest {

    private UbicacionArticuloRepository repository;
    private SaveUbicacionArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(UbicacionArticuloRepository.class);
        useCase = new SaveUbicacionArticuloUseCaseImpl(repository);
    }

    @Test
    void ubicacionNula_400SinEscribir() {
        assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().articuloId(2L).build()))
                .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("ubicacionId"));
        verifyNoInteractions(repository);
    }

    @Test
    void articuloNulo_400SinEscribir() {
        assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().ubicacionId(1).build()))
                .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("articuloId"));
        verifyNoInteractions(repository);
    }

    @Test
    void cuentaFueraDeDecimal11_0_400SinEscribir() {
        for (var cuenta : new String[]{"1e999999999", "100000000000", "51010101.5"}) {
            assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal(cuenta)).build()))
                    .as(cuenta)
                    .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("numeroCuenta"));
        }
        verifyNoInteractions(repository);
    }

    @Test
    void idsFueraDeRango_400SinEscribir() {
        assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().ubicacionId(0).articuloId(2L).build()))
                .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("ubicacionId"));
        for (long id : new long[]{0L, 2147483648L, Long.MAX_VALUE}) {
            assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().ubicacionId(1).articuloId(id).build()))
                    .as("articuloId " + id)
                    .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("articuloId"));
        }
        verifyNoInteractions(repository);
    }

    @Test
    void cuentaConExponenteExtremo_400_yCeroConEscalaEnorme_seNormaliza() {
        assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal("100e2147483647")).build()))
                .isInstanceOf(UbicacionArticuloValidationException.class);
        var pedido = UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal("0e-1000000000")).build();
        when(repository.save(pedido)).thenReturn(pedido);

        assertThat(useCase.save(pedido).getNumeroCuenta().scale()).isZero();
    }

    @Test
    void cuentaNula_seAcepta() {
        var pedido = UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).build();
        when(repository.save(pedido)).thenReturn(pedido);

        assertThat(useCase.save(pedido)).isSameAs(pedido);
        verify(repository).save(pedido);
    }
}
