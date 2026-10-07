package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.GetArticuloByIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.in.GetUbicacionByIdUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.AsignacionGuardada;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SaveUbicacionArticuloUseCaseImplTest {

    private UbicacionArticuloRepository repository;
    private GetUbicacionByIdUseCase ubicaciones;
    private GetArticuloByIdUseCase articulos;
    private GetCuentaByNumeroCuentaUseCase cuentas;
    private RegistrarEscrituraHistorialUseCase historial;
    private SaveUbicacionArticuloUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(UbicacionArticuloRepository.class);
        ubicaciones = mock(GetUbicacionByIdUseCase.class);
        articulos = mock(GetArticuloByIdUseCase.class);
        cuentas = mock(GetCuentaByNumeroCuentaUseCase.class);
        historial = mock(RegistrarEscrituraHistorialUseCase.class);
        useCase = new SaveUbicacionArticuloUseCaseImpl(repository, ubicaciones, articulos, cuentas, historial);
        when(ubicaciones.getUbicacionById(anyInt())).thenAnswer(inv -> Optional.of(Ubicacion.builder().ubicacionId(inv.getArgument(0)).build()));
        when(articulos.getArticuloById(anyLong())).thenAnswer(inv -> Optional.of(Articulo.builder().articuloId(inv.getArgument(0)).build()));
        when(cuentas.getCuentaByNumeroCuenta(any())).thenAnswer(inv -> Optional.of(Cuenta.builder().numeroCuenta(inv.getArgument(0)).build()));
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
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(null, pedido));

        assertThat(useCase.save(pedido).getNumeroCuenta().scale()).isZero();
    }

    @Test
    void cuentaNula_seAcepta() {
        var pedido = UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).build();
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(null, pedido));

        assertThat(useCase.save(pedido)).isSameAs(pedido);
        verify(repository).save(pedido);
    }

    @Test
    void ubicacionInexistente_400SinEscribir() {
        when(ubicaciones.getUbicacionById(1051)).thenReturn(Optional.empty());

        rechaza(UbicacionArticulo.builder().ubicacionId(1051).articuloId(2L).build(), "ubicacionId", "La ubicación indicada no existe.");
    }

    @Test
    void articuloInexistente_400SinEscribir() {
        when(articulos.getArticuloById(999404L)).thenReturn(Optional.empty());

        rechaza(UbicacionArticulo.builder().ubicacionId(1).articuloId(999404L).build(), "articuloId", "El artículo indicado no existe.");
    }

    @Test
    void cuentaInexistente_400SinEscribir() {
        when(cuentas.getCuentaByNumeroCuenta(new BigDecimal("99999999999"))).thenReturn(Optional.empty());

        rechaza(UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal("99999999999")).build(),
                "numeroCuenta", "La cuenta indicada no existe en el plan de cuentas.");
    }

    @Test
    void variasReferenciasInexistentes_seInformaLaPrimeraDelPedido() {
        when(ubicaciones.getUbicacionById(1051)).thenReturn(Optional.empty());
        when(articulos.getArticuloById(999404L)).thenReturn(Optional.empty());

        rechaza(UbicacionArticulo.builder().ubicacionId(1051).articuloId(999404L).build(), "ubicacionId", "La ubicación indicada no existe.");
    }

    @Test
    void referenciasExistentes_seValidanAntesDeEscribir_conLaCuentaNormalizada() {
        var pedido = UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal("5.101E+7")).build();
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(null, pedido));

        useCase.save(pedido);

        InOrder orden = inOrder(ubicaciones, articulos, cuentas, repository);
        orden.verify(ubicaciones).getUbicacionById(1);
        orden.verify(articulos).getArticuloById(2L);
        orden.verify(cuentas).getCuentaByNumeroCuenta(new BigDecimal("51010000"));
        orden.verify(repository).save(pedido);
    }

    @Test
    void cuentaNula_noSeConsulta() {
        var pedido = UbicacionArticulo.builder().ubicacionId(1).articuloId(2L).build();
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(null, pedido));

        useCase.save(pedido);

        verifyNoInteractions(cuentas);
    }

    @Test
    void datoMalFormado_400SinConsultarReferencias() {
        assertThatThrownBy(() -> useCase.save(UbicacionArticulo.builder().ubicacionId(1).articuloId(0L).build()))
                .isInstanceOf(UbicacionArticuloValidationException.class);
        verifyNoInteractions(ubicaciones, articulos, cuentas, repository);
    }

    @Test
    void vinculoNuevo_registraAltaConLaClaveDelPar() {
        var pedido = UbicacionArticulo.builder().ubicacionId(3).articuloId(5L).numeroCuenta(new BigDecimal("51010000")).build();
        var guardada = UbicacionArticulo.builder().ubicacionArticuloId(77L).ubicacionId(3).articuloId(5L)
                .numeroCuenta(new BigDecimal("51010000")).cuenta(Cuenta.builder().numeroCuenta(new BigDecimal("51010000")).build()).build();
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(null, guardada));

        assertThat(useCase.save(pedido)).isSameAs(guardada);

        verify(historial).registrarAlta("ubicacion_articulo", "3:5", new UbicacionArticuloEstado(77L, 3, 5L, new BigDecimal("51010000")));
    }

    @Test
    void vinculoExistenteConOtraCuenta_registraEdicion() {
        var pedido = UbicacionArticulo.builder().ubicacionId(3).articuloId(5L).build();
        var anterior = UbicacionArticulo.builder().ubicacionArticuloId(77L).ubicacionId(3).articuloId(5L).numeroCuenta(new BigDecimal("51010000")).build();
        var guardada = UbicacionArticulo.builder().ubicacionArticuloId(77L).ubicacionId(3).articuloId(5L).build();
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(anterior, guardada));

        useCase.save(pedido);

        verify(historial).registrarEdicion("ubicacion_articulo", "3:5",
                new UbicacionArticuloEstado(77L, 3, 5L, new BigDecimal("51010000")), new UbicacionArticuloEstado(77L, 3, 5L, null));
    }

    @Test
    void asignacionRepetidaSinCambios_noRegistra() {
        var pedido = UbicacionArticulo.builder().ubicacionId(3).articuloId(5L).numeroCuenta(new BigDecimal("51010000")).build();
        var igual = UbicacionArticulo.builder().ubicacionArticuloId(77L).ubicacionId(3).articuloId(5L).numeroCuenta(new BigDecimal("51010000")).build();
        when(repository.save(pedido)).thenReturn(new AsignacionGuardada(igual, igual));

        useCase.save(pedido);

        verifyNoInteractions(historial);
    }

    @Test
    void asignacionRechazada_noRegistra() {
        when(ubicaciones.getUbicacionById(1051)).thenReturn(Optional.empty());

        rechaza(UbicacionArticulo.builder().ubicacionId(1051).articuloId(2L).build(), "ubicacionId", "La ubicación indicada no existe.");
        verifyNoInteractions(historial);
    }

    private void rechaza(UbicacionArticulo pedido, String campo, String mensaje) {
        assertThatThrownBy(() -> useCase.save(pedido))
                .isInstanceOfSatisfying(UbicacionArticuloValidationException.class, ex -> {
                    assertThat(ex.getCampo()).isEqualTo(campo);
                    assertThat(ex.getMessage()).isEqualTo(mensaje);
                });
        verify(repository, never()).save(any());
    }
}
