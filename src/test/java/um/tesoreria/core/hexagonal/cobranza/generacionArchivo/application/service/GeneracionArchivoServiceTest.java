package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CanalGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudAnulacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudReprogramacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.CuotaDeudaRepository;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.DebitoGeneracionRepository;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.PagarFileGenerator;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.PayPerTicFileGenerator;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeneracionArchivoServiceTest {

    @Mock
    private CuotaDeudaRepository cuotaDeudaRepository;
    @Mock
    private DebitoGeneracionRepository debitoGeneracionRepository;
    @Mock
    private PagarFileGenerator pagarFileGenerator;
    @Mock
    private PayPerTicFileGenerator payPerTicFileGenerator;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraPagoService chequeraPagoService;

    private GeneracionArchivoService service;

    @BeforeEach
    void setUp() {
        service = new GeneracionArchivoService(cuotaDeudaRepository, debitoGeneracionRepository,
                pagarFileGenerator, payPerTicFileGenerator, chequeraCuotaService, chequeraSerieService,
                chequeraPagoService);
        when(chequeraSerieService.findByUnique(any(), any(), any())).thenReturn(ChequeraSerie.builder()
                .documentoId(12345678)
                .personaId(new BigDecimal("45719365"))
                .tipoChequera(TipoChequera.builder().claseChequeraId(1).build())
                .build());
        when(chequeraCuotaService.findByUnique(any(), any(), any(), any(), any(), any()))
                .thenAnswer(invocation -> ChequeraCuota.builder()
                        .chequeraCuotaId(99L)
                        .facultadId(6)
                        .tipoChequeraId(2)
                        .chequeraSerieId(123L)
                        .productoId(1)
                        .alternativaId(1)
                        .cuotaId(invocation.getArgument(5))
                        .vencimiento1(OffsetDateTime.parse("2026-03-15T00:00:00Z"))
                        .vencimiento2(OffsetDateTime.parse("2026-03-25T00:00:00Z"))
                        .vencimiento3(OffsetDateTime.parse("2026-04-04T00:00:00Z"))
                        .importe1(new BigDecimal("123.45"))
                        .baja(invocation.getArgument(5).equals(2) ? (byte) 1 : (byte) 0)
                        .build());
        when(chequeraPagoService.isPagado(any(), any(), any(), any(), any(), any())).thenReturn(false);
    }

    @Test
    void generaDebitoDirectoYMarcaSoloLosDebitosIncluidos() {
        when(debitoGeneracionRepository.findPendientes(any(), any(), eq(false), eq(false), eq(2)))
                .thenReturn(List.of(debito(1L, 1, "721234567890123456789"),
                        debito(2L, 2, "0720000012345678901234")));

        ResultadoGeneracion resultado = service.generar(solicitud(CanalGeneracion.DEBITO_DIRECTO));

        assertThat(resultado.registros()).isEqualTo(1);
        assertThat(resultado.total()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(new String(resultado.bytes(), StandardCharsets.ISO_8859_1)).startsWith("30518594466");
        verify(debitoGeneracionRepository).marcarFechaEnvio(eq(1L), any());
        verify(debitoGeneracionRepository, never()).marcarFechaEnvio(eq(2L), any());
        verify(debitoGeneracionRepository).actualizarCbu(eq(1L), eq("0721234567890123456789"));
    }

    @Test
    void anularOmiteLosDebitosQueYaTienenPago() {
        when(debitoGeneracionRepository.findByFechaEnvio(any(), eq(false), eq(false), eq(2)))
                .thenReturn(List.of(debito(1L, 1, "0720000012345678901234"),
                        debito(2L, 2, "0720000012345678901234")));
        when(chequeraPagoService.isPagado(any(), any(), any(), any(), any(), eq(2))).thenReturn(true);

        ResultadoOperacionDebito resultado = service.anular(new SolicitudAnulacion(
                CanalGeneracion.DEBITO_DIRECTO, OffsetDateTime.parse("2026-03-15T00:00:00Z"),
                false, false));

        assertThat(resultado.registros()).isEqualTo(1);
        assertThat(resultado.omitidos()).isEqualTo(1);
        verify(debitoGeneracionRepository).marcarFechaEnvio(eq(1L), isNull());
        verify(debitoGeneracionRepository, never()).marcarFechaEnvio(eq(2L), isNull());
    }

    @Test
    void reprogramarActualizaLaCuotaYLosDebitosAsociados() {
        OffsetDateTime nuevaFecha = OffsetDateTime.parse("2026-04-01T00:00:00Z");
        when(debitoGeneracionRepository.findPendientes(any(), any(), eq(false), eq(false), eq(2)))
                .thenReturn(List.of(debito(1L, 1, "0720000012345678901234")));
        when(debitoGeneracionRepository.findAllAsociados(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(debito(1L, 1, "0720000012345678901234")));

        ResultadoOperacionDebito resultado = service.reprogramar(new SolicitudReprogramacion(
                OffsetDateTime.parse("2026-03-01T00:00:00Z"),
                OffsetDateTime.parse("2026-03-31T23:59:59Z"), false, false, nuevaFecha));

        assertThat(resultado.registros()).isEqualTo(1);
        assertThat(resultado.omitidos()).isZero();
        ArgumentCaptor<ChequeraCuota> cuota = ArgumentCaptor.forClass(ChequeraCuota.class);
        verify(chequeraCuotaService).update(cuota.capture(), eq(99L));
        assertThat(cuota.getValue().getVencimiento1()).isEqualTo(nuevaFecha);
        assertThat(cuota.getValue().getVencimiento2()).isEqualTo(nuevaFecha.plusDays(1));
        assertThat(cuota.getValue().getVencimiento3()).isEqualTo(OffsetDateTime.parse("2026-04-04T00:00:00Z"));
        verify(debitoGeneracionRepository).actualizarFechaVencimiento(1L, nuevaFecha);
    }

    private SolicitudGeneracion solicitud(CanalGeneracion canal) {
        return new SolicitudGeneracion(canal, OffsetDateTime.parse("2026-03-01T00:00:00Z"),
                OffsetDateTime.parse("2026-03-31T23:59:59Z"), null, false, false, BigDecimal.ZERO);
    }

    private DebitoPendiente debito(Long debitoId, Integer cuotaId, String cbu) {
        return DebitoPendiente.builder()
                .debitoId(debitoId)
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .productoId(1)
                .alternativaId(1)
                .cuotaId(cuotaId)
                .debitoTipoId(2)
                .cbu(cbu)
                .fechaVencimiento(OffsetDateTime.parse("2026-03-31T00:00:00Z"))
                .build();
    }

}
