package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

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
    private GetCuentaByNumeroCuentaUseCase cuentas;
    private RegistrarEscrituraHistorialUseCase historial;
    private UpdateArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ArticuloRepository.class);
        cuentas = mock(GetCuentaByNumeroCuentaUseCase.class);
        historial = mock(RegistrarEscrituraHistorialUseCase.class);
        useCase = new UpdateArticuloUseCaseImpl(repository, cuentas, historial);
        when(cuentas.getCuentaByNumeroCuenta(any())).thenAnswer(inv -> Optional.of(Cuenta.builder().numeroCuenta(inv.getArgument(0)).build()));
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

    @Test
    void cuentaInexistente_400SinBloquearNiEscribir() {
        when(cuentas.getCuentaByNumeroCuenta(new BigDecimal("99999999999"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateArticulo(7L, Articulo.builder().numeroCuenta(new BigDecimal("99999999999")).build()))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> {
                    assertThat(ex.getCampo()).isEqualTo("numeroCuenta");
                    assertThat(ex.getMessage()).isEqualTo("La cuenta indicada no existe en el plan de cuentas.");
                });
        verifyNoInteractions(repository);
    }

    @Test
    void cuentaExistente_seAplica() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        var guardado = useCase.updateArticulo(7L, Articulo.builder().numeroCuenta(new BigDecimal("5.101E+7")).build());

        verify(cuentas).getCuentaByNumeroCuenta(new BigDecimal("51010000"));
        assertThat(guardado.getNumeroCuenta()).isEqualTo(new BigDecimal("51010000"));
    }

    @Test
    void sinCuentaEnLosCambios_noSeConsulta() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        useCase.updateArticulo(7L, Articulo.builder().nombre("Nuevo").build());

        verifyNoInteractions(cuentas);
    }

    @Test
    void edicion_registraAntesDeLaLecturaBloqueadaYDespues() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        useCase.updateArticulo(7L, Articulo.builder().nombre("Nuevo").habilitado((byte) 0).build());

        var antes = ArticuloEstado.de(actual());
        var despues = new ArticuloEstado(7L, "Nuevo", "D", "U", new BigDecimal("10.00"), (byte) 0, 0L,
                new BigDecimal("51010101"), "gasto", (byte) 1, (byte) 0);
        verify(historial).registrarEdicion("articulo", "7", antes, despues);
    }

    @Test
    void edicionSinCambios_noRegistra() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(actual()));

        useCase.updateArticulo(7L, Articulo.builder().nombre("Viejo").precio(new BigDecimal("10.000")).build());

        verifyNoInteractions(historial);
    }

    @Test
    void edicionDeInexistente_noRegistra() {
        when(repository.findByIdForUpdate(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateArticulo(8L, Articulo.builder().nombre("X").build())).isInstanceOf(ArticuloException.class);
        verifyNoInteractions(historial);
    }

    private static Articulo actual() {
        return Articulo.builder().articuloId(7L).nombre("Viejo").descripcion("D").unidad("U").precio(BigDecimal.TEN)
                .inventariable((byte) 0).stockMinimo(0L).numeroCuenta(new BigDecimal("51010101")).tipo("gasto")
                .directo((byte) 1).habilitado((byte) 1).build();
    }
}
