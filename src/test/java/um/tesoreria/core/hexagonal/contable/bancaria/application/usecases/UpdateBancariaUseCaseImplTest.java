package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBancariaUseCaseImplTest {
    static final BigDecimal CUENTA = new BigDecimal("11101010001");

    @Mock BancariaRepository repository;
    @Mock BancariaReferenciasPort referencias;
    @Mock BancariaHistorialPort historial;
    UpdateBancariaUseCaseImpl useCase;

    Bancaria guardada = Bancaria.builder().bancariaId(8L).banco("Santander").numero("123-4").cbu("0070999000001234567891")
            .titular("Universidad").cuit("30-51859446-6").numeroCuenta(CUENTA).proveedorId(12)
            .created(LocalDateTime.parse("2024-01-01T10:00:00")).updated(LocalDateTime.parse("2026-10-01T09:30:15"))
            .uuid("0".repeat(32)).build();

    @BeforeEach
    void preparar() {
        useCase = new UpdateBancariaUseCaseImpl(repository, referencias, historial);
        lenient().when(repository.findWithLockByBancariaId(8L)).thenReturn(Optional.of(guardada));
        lenient().when(repository.update(any())).thenAnswer(i -> i.getArgument(0, Bancaria.class)
                .toBuilder().updated(LocalDateTime.parse("2026-10-23T12:00:00")).build());
        lenient().when(referencias.existeCuenta(any())).thenReturn(true);
        lenient().when(referencias.existeProveedor(any())).thenReturn(true);
    }

    BancariaSolicitudInvalidaException rechazo(BancariaCambios cambios) {
        try {
            useCase.updateBancaria(8L, cambios);
        } catch (BancariaSolicitudInvalidaException ex) {
            return ex;
        }
        throw new AssertionError("no se rechazó");
    }

    @Test
    void leeLaFilaConLockSinHacerUnaLecturaPreviaSinBloqueo() {
        useCase.updateBancaria(8L, BancariaCambios.builder().titular(Campo.de("Otro")).build());
        verify(repository).findWithLockByBancariaId(8L);
        verify(repository, never()).findByBancariaId(any());
    }

    @Test
    void ausenteConservaYSoloCambiaLoEnviadoRegistrandoLaEdicion() {
        Bancaria resultado = useCase.updateBancaria(8L, BancariaCambios.builder().titular(Campo.de("Otro titular")).build());
        assertThat(resultado.getTitular()).isEqualTo("Otro titular");
        assertThat(resultado.getBanco()).isEqualTo("Santander");
        assertThat(resultado.getCbu()).isEqualTo("0070999000001234567891");
        assertThat(resultado.getProveedorId()).isEqualTo(12);
        assertThat(resultado.getNumeroCuenta()).isEqualByComparingTo(CUENTA);
        verify(historial).registrarEdicion(guardada, resultado);
    }

    @Test
    void nullExplicitoVaciaLosTextosYElProveedor() {
        Bancaria resultado = useCase.updateBancaria(8L, BancariaCambios.builder().cbu(Campo.de(null)).titular(Campo.de(null))
                .banco(Campo.de(null)).numero(Campo.de(null)).cuit(Campo.de(null)).proveedorId(Campo.de(null)).build());
        assertThat(resultado.getCbu()).isEmpty();
        assertThat(resultado.getTitular()).isEmpty();
        assertThat(resultado.getBanco()).isEmpty();
        assertThat(resultado.getNumero()).isEmpty();
        assertThat(resultado.getCuit()).isEmpty();
        assertThat(resultado.getProveedorId()).isNull();
        assertThat(resultado.getNumeroCuenta()).isEqualByComparingTo(CUENTA);
    }

    @Test
    void nullEnLaCuentaContableEsCuentaRequeridaYNoToca() {
        assertThat(rechazo(BancariaCambios.builder().numeroCuenta(Campo.de(null)).build()).getCodigo())
                .isEqualTo(Codigo.CUENTA_REQUERIDA);
        verify(repository, never()).update(any());
        verifyNoInteractions(historial);
    }

    @Test
    void sinCambiosEfectivosDevuelveLoGuardadoSinEscribirNiHistorial() {
        assertThat(useCase.updateBancaria(8L, BancariaCambios.builder().build())).isSameAs(guardada);
        verify(repository, never()).update(any());
        verifyNoInteractions(historial);
    }

    @Test
    void reenviarTodoIgualEsSinCambiosAunqueElCuitVengaSinGuionesOLaCuentaConOtraEscala() {
        BancariaCambios completo = BancariaCambios.builder().banco(Campo.de("Santander")).numero(Campo.de("123-4"))
                .cbu(Campo.de("0070999000001234567891")).titular(Campo.de("Universidad")).cuit(Campo.de("30518594466"))
                .numeroCuenta(Campo.de(new BigDecimal("11101010001.0"))).proveedorId(Campo.de(12)).build();
        assertThat(useCase.updateBancaria(8L, completo)).isSameAs(guardada);
        verify(repository, never()).update(any());
        verifyNoInteractions(historial);
    }

    @Test
    void elIdDelCuerpoDistintoAlDeLaUrlEsRechazadoYAntesDeLeer() {
        BancariaSolicitudInvalidaException ex = rechazo(BancariaCambios.builder().bancariaId(9L).build());
        assertThat(ex.getCodigo()).isEqualTo(Codigo.ID_NO_COINCIDE);
        assertThat(ex.getMessage()).contains("9").contains("8");
        verifyNoInteractions(historial);
        verify(repository, never()).findWithLockByBancariaId(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -3, 8})
    void idDelCuerpoAusenteNullNoPositivoOIgualAlDeLaUrlSeAcepta(Long id) {
        assertThat(useCase.updateBancaria(8L, BancariaCambios.builder().bancariaId(id).build())).isSameAs(guardada);
    }

    @Test
    void cuentaInexistenteNoSeEncuentra404() {
        when(repository.findWithLockByBancariaId(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.updateBancaria(8L, BancariaCambios.builder().build()))
                .isInstanceOf(BancariaNoEncontradaException.class);
        verifyNoInteractions(historial);
    }

    @Test
    void cuitHistoricoInvalidoQueNoSeTocaNoSeRevalida() {
        Bancaria conCuitViejo = guardada.toBuilder().cuit("20-12345678-0").cbu("123").build();
        when(repository.findWithLockByBancariaId(8L)).thenReturn(Optional.of(conCuitViejo));
        Bancaria resultado = useCase.updateBancaria(8L, BancariaCambios.builder().titular(Campo.de("Nuevo"))
                .cuit(Campo.de("20-12345678-0")).cbu(Campo.de("123")).build());
        assertThat(resultado.getCuit()).isEqualTo("20-12345678-0");
        assertThat(resultado.getCbu()).isEqualTo("123");
        assertThat(resultado.getTitular()).isEqualTo("Nuevo");
    }

    @Test
    void cambiarElCuitOElCbuAUnoInvalidoEsRechazado() {
        assertThat(rechazo(BancariaCambios.builder().cuit(Campo.de("30-51859446-5")).build()).getCodigo()).isEqualTo(Codigo.CUIT_INVALIDO);
        assertThat(rechazo(BancariaCambios.builder().cbu(Campo.de("0070999000001234567892")).build()).getCodigo()).isEqualTo(Codigo.CBU_INVALIDO);
    }

    @Test
    void cambiarElCuitSinGuionesLoGuardaConGuiones() {
        Bancaria resultado = useCase.updateBancaria(8L, BancariaCambios.builder().cuit(Campo.de("20123456786")).build());
        assertThat(resultado.getCuit()).isEqualTo("20-12345678-6");
    }

    @Test
    void textosFueraDeReglaSonRechazadosEnLaEdicion() {
        assertThat(rechazo(BancariaCambios.builder().banco(Campo.de("b".repeat(65))).build()).getCodigo()).isEqualTo(Codigo.TEXTO_INVALIDO);
        assertThat(rechazo(BancariaCambios.builder().titular(Campo.de("😀")).build()).getCodigo()).isEqualTo(Codigo.TEXTO_INVALIDO);
    }

    @Test
    void laReferenciaSoloSeConsultaSiCambia() {
        useCase.updateBancaria(8L, BancariaCambios.builder().numeroCuenta(Campo.de(CUENTA)).proveedorId(Campo.de(12))
                .titular(Campo.de("Otro")).build());
        verifyNoInteractions(referencias);

        useCase.updateBancaria(8L, BancariaCambios.builder().numeroCuenta(Campo.de(new BigDecimal("11101010002"))).proveedorId(Campo.de(13)).build());
        verify(referencias).existeCuenta(new BigDecimal("11101010002"));
        verify(referencias).existeProveedor(13);
    }

    @Test
    void cuentaOProveedorNuevosInexistentesSonRechazados() {
        when(referencias.existeCuenta(any())).thenReturn(false);
        assertThat(rechazo(BancariaCambios.builder().numeroCuenta(Campo.de(new BigDecimal("1"))).build()).getCodigo())
                .isEqualTo(Codigo.CUENTA_INEXISTENTE);
        when(referencias.existeProveedor(99)).thenReturn(false);
        assertThat(rechazo(BancariaCambios.builder().proveedorId(Campo.de(99)).build()).getCodigo())
                .isEqualTo(Codigo.PROVEEDOR_INEXISTENTE);
        verify(repository, never()).update(any());
    }

    @Test
    void laPrecondicionUpdatedDistintaEsCambioConcurrenteYNoEscribe() {
        assertThatThrownBy(() -> useCase.updateBancaria(8L, BancariaCambios.builder()
                .updated(LocalDateTime.parse("2026-10-01T09:30:14")).titular(Campo.de("Viejo")).build()))
                .isInstanceOfSatisfying(BancariaConflictoException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo(BancariaConflictoException.Codigo.CAMBIO_CONCURRENTE));
        verify(repository, never()).update(any());
        verifyNoInteractions(historial);
    }

    @Test
    void laPrecondicionUpdatedIgualPasaAunConFraccionDeSegundo() {
        Bancaria resultado = useCase.updateBancaria(8L, BancariaCambios.builder()
                .updated(LocalDateTime.parse("2026-10-01T09:30:15.750")).titular(Campo.de("Nuevo")).build());
        assertThat(resultado.getTitular()).isEqualTo("Nuevo");
    }

    @Test
    void elHistorialDeEdicionLlevaElEstadoAnteriorLeidoBajoElLock() {
        Bancaria resultado = useCase.updateBancaria(8L, BancariaCambios.builder().banco(Campo.de("Nación")).build());
        verify(historial).registrarEdicion(guardada, resultado);
        verifyNoMoreInteractions(historial);
        assertThat(guardada.getBanco()).isEqualTo("Santander");
    }
}
