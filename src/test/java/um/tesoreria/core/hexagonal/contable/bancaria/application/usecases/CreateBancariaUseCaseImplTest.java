package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBancariaUseCaseImplTest {
    static final BigDecimal CUENTA = new BigDecimal("11101010001");

    @Mock BancariaRepository repository;
    @Mock BancariaReferenciasPort referencias;
    @Mock BancariaHistorialPort historial;
    CreateBancariaUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-23T15:00:00Z"), ZoneOffset.UTC);
        useCase = new CreateBancariaUseCaseImpl(repository, referencias, historial, clock);
        lenient().when(referencias.existeCuenta(any())).thenReturn(true);
        lenient().when(referencias.existeProveedor(any())).thenReturn(true);
        lenient().when(repository.create(any())).thenAnswer(i -> i.getArgument(0, Bancaria.class).toBuilder().bancariaId(21L).build());
    }

    BancariaCambios.BancariaCambiosBuilder pedido() {
        return BancariaCambios.builder().numeroCuenta(Campo.de(CUENTA));
    }

    Bancaria guardada() {
        ArgumentCaptor<Bancaria> captor = ArgumentCaptor.forClass(Bancaria.class);
        verify(repository).create(captor.capture());
        return captor.getValue();
    }

    BancariaSolicitudInvalidaException rechazo(BancariaCambios pedido) {
        assertThatThrownBy(() -> useCase.createBancaria(pedido)).isInstanceOf(BancariaSolicitudInvalidaException.class);
        try {
            useCase.createBancaria(pedido);
        } catch (BancariaSolicitudInvalidaException ex) {
            return ex;
        }
        throw new AssertionError();
    }

    @Test
    void altaMinimaGuardaVaciosCreatedYUuidSinGuionesYRegistraHistorial() {
        Bancaria creada = useCase.createBancaria(pedido().build());
        Bancaria guardada = guardada();
        assertThat(guardada.getBanco()).isEmpty();
        assertThat(guardada.getNumero()).isEmpty();
        assertThat(guardada.getCbu()).isEmpty();
        assertThat(guardada.getTitular()).isEmpty();
        assertThat(guardada.getCuit()).isEmpty();
        assertThat(guardada.getProveedorId()).isNull();
        assertThat(guardada.getCreated()).isEqualTo(LocalDateTime.parse("2026-10-23T15:00:00"));
        assertThat(guardada.getUuid()).matches("[0-9a-f]{32}");
        assertThat(creada.getBancariaId()).isEqualTo(21L);
        verify(historial).registrarAlta(creada);
    }

    @Test
    void textosEnNullValenVacioIgualQueAusentes() {
        useCase.createBancaria(pedido().banco(Campo.de(null)).titular(Campo.de(null)).cbu(Campo.de(null)).cuit(Campo.de(null)).build());
        Bancaria guardada = guardada();
        assertThat(guardada.getBanco()).isEmpty();
        assertThat(guardada.getTitular()).isEmpty();
        assertThat(guardada.getCbu()).isEmpty();
        assertThat(guardada.getCuit()).isEmpty();
    }

    @Test
    void idMayorACeroEsRechazado() {
        BancariaSolicitudInvalidaException ex = rechazo(pedido().bancariaId(5L).build());
        assertThat(ex.getCodigo()).isEqualTo(Codigo.ID_NO_PERMITIDO);
        assertThat(ex.getMessage()).contains("PUT /bancaria/{id}");
        verifyNoInteractions(historial);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void idCeroONegativoSeTrataComoOmitido(long id) {
        useCase.createBancaria(pedido().bancariaId(id).build());
        assertThat(guardada().getBancariaId()).isNull();
    }

    @Test
    void sinCuentaContableOConNullEsCuentaRequerida() {
        for (BancariaCambios sinCuenta : new BancariaCambios[]{
                BancariaCambios.builder().build(),
                BancariaCambios.builder().numeroCuenta(Campo.de(null)).build()}) {
            assertThat(rechazo(sinCuenta).getCodigo()).isEqualTo(Codigo.CUENTA_REQUERIDA);
        }
        verify(repository, never()).create(any());
    }

    @Test
    void textoDe64CaracteresPasaYDe65EsRechazado() {
        useCase.createBancaria(pedido().banco(Campo.de("b".repeat(64))).build());
        assertThat(guardada().getBanco()).hasSize(64);
        BancariaSolicitudInvalidaException ex = rechazo(pedido().titular(Campo.de("t".repeat(65))).build());
        assertThat(ex.getCodigo()).isEqualTo(Codigo.TEXTO_INVALIDO);
        assertThat(ex.getMessage()).startsWith("titular");
        assertThat(rechazo(pedido().numero(Campo.de("1".repeat(65))).build()).getMessage()).startsWith("numero");
    }

    @Test
    void ñTildesYCaracteresDeCp1252SeAceptanYUnEmojiNo() {
        String acepta = "Caja de Ahorro Ñandú á é í ó ú ü € “comillas”";
        useCase.createBancaria(pedido().titular(Campo.de(acepta)).build());
        assertThat(guardada().getTitular()).isEqualTo(acepta);
        assertThat(rechazo(pedido().banco(Campo.de("Banco 🏦")).build()).getCodigo()).isEqualTo(Codigo.TEXTO_INVALIDO);
        assertThat(rechazo(pedido().banco(Campo.de("Банк")).build()).getCodigo()).isEqualTo(Codigo.TEXTO_INVALIDO);
    }

    @Test
    void elTextoSeGuardaTalCualSinRecortarEspacios() {
        useCase.createBancaria(pedido().banco(Campo.de("Caja Moneda Extranjera ")).build());
        assertThat(guardada().getBanco()).isEqualTo("Caja Moneda Extranjera ");
    }

    @Test
    void cuitSinGuionesSeGuardaConGuionesYElSinDatoYElVacioSeAceptan() {
        useCase.createBancaria(pedido().cuit(Campo.de("30518594466")).build());
        assertThat(guardada().getCuit()).isEqualTo("30-51859446-6");
    }

    @Test
    void cuitInvalidoEsRechazado() {
        assertThat(rechazo(pedido().cuit(Campo.de("30-51859446-5")).build()).getCodigo()).isEqualTo(Codigo.CUIT_INVALIDO);
    }

    @Test
    void elCuitSinDatoSeAcepta() {
        useCase.createBancaria(pedido().cuit(Campo.de("00-00000000-0")).build());
        assertThat(guardada().getCuit()).isEqualTo("00-00000000-0");
    }

    @Test
    void cbuValidoPasaEInvalidoEsRechazado() {
        useCase.createBancaria(pedido().cbu(Campo.de("0070999000001234567891")).build());
        assertThat(guardada().getCbu()).isEqualTo("0070999000001234567891");
        assertThat(rechazo(pedido().cbu(Campo.de("0070999000001234567892")).build()).getCodigo()).isEqualTo(Codigo.CBU_INVALIDO);
    }

    @Test
    void cuentaContableInexistenteEsRechazadaSinGuardarNiHistorial() {
        when(referencias.existeCuenta(CUENTA)).thenReturn(false);
        BancariaSolicitudInvalidaException ex = rechazo(pedido().build());
        assertThat(ex.getCodigo()).isEqualTo(Codigo.CUENTA_INEXISTENTE);
        verify(repository, never()).create(any());
        verifyNoInteractions(historial);
    }

    @Test
    void proveedorInexistenteEsRechazadoYUnoExistenteSeGuarda() {
        useCase.createBancaria(pedido().proveedorId(Campo.de(12)).build());
        assertThat(guardada().getProveedorId()).isEqualTo(12);
        when(referencias.existeProveedor(99)).thenReturn(false);
        assertThat(rechazo(pedido().proveedorId(Campo.de(99)).build()).getCodigo()).isEqualTo(Codigo.PROVEEDOR_INEXISTENTE);
    }
}
