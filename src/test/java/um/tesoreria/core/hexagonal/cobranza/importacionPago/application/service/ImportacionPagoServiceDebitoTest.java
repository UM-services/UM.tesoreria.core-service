package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service;

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
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.DebitoCobranza;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoBancoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoImportadoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.DebitoCobranzaRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ParserArchivoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ReemplazoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser.DebitoDirectoFileParser;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser.DebitoVisaFileParser;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportacionPagoServiceDebitoTest {

    @Mock
    private ArchivoImportadoRepository archivoImportadoRepository;
    @Mock
    private ArchivoBancoRepository archivoBancoRepository;
    @Mock
    private ReemplazoRepository reemplazoRepository;
    @Mock
    private PayPerTicRendicionRepository payPerTicRendicionRepository;
    @Mock
    private DebitoCobranzaRepository debitoCobranzaRepository;
    @Mock
    private ChequeraSerieService chequeraSerieService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraPagoService chequeraPagoService;

    private ImportacionPagoService service;
    private final List<ArchivoBanco> filasGuardadas = new ArrayList<>();

    @BeforeEach
    void setUp() {
        List<ParserArchivoPago> parsers = List.of(new DebitoDirectoFileParser(), new DebitoVisaFileParser());
        service = new ImportacionPagoService(parsers, archivoImportadoRepository, archivoBancoRepository,
                reemplazoRepository, payPerTicRendicionRepository, debitoCobranzaRepository,
                chequeraSerieService, chequeraCuotaService, chequeraPagoService);

        when(archivoImportadoRepository.existsByNombre(any())).thenReturn(false);
        when(archivoImportadoRepository.save(any())).thenAnswer(invocation -> {
            ArchivoImportado cabecera = invocation.getArgument(0);
            cabecera.setId(17);
            return cabecera;
        });
        AtomicLong secuencia = new AtomicLong();
        when(archivoBancoRepository.save(any())).thenAnswer(invocation -> {
            ArchivoBanco fila = invocation.getArgument(0);
            if (fila.getId() == null) {
                fila.setId(secuencia.incrementAndGet());
            }
            filasGuardadas.add(fila);
            return fila;
        });
        when(chequeraSerieService.findByUnique(any(), any(), any())).thenReturn(ChequeraSerie.builder()
                .chequeraId(1L)
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .alternativaId(1)
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
                        .mes(3)
                        .anho(2026)
                        .vencimiento1(OffsetDateTime.parse("2026-03-15T00:00:00Z"))
                        .importe1(new BigDecimal("123.45"))
                        .build());
        when(chequeraPagoService.nextOrden(any(), any(), any(), any(), any(), any())).thenReturn(0);
        when(chequeraPagoService.create(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraPagoService.isPagado(any(), any(), any(), any(), any(), any())).thenReturn(true);
        when(chequeraCuotaService.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(chequeraCuotaService.calculateTotalCuotasActivas(any(), any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(chequeraCuotaService.calculateTotalCuotasPagadas(any(), any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
    }

    @Test
    void importaDebitoDirectoAcreditadoYRechazado() {
        when(debitoCobranzaRepository.findById(123L)).thenReturn(Optional.of(debito(123L, 1)));
        when(debitoCobranzaRepository.findById(124L)).thenReturn(Optional.of(debito(124L, 2)));

        ResultadoImportacion resultado = service.importar(SolicitudImportacion.deArchivo(
                CanalImportacion.DEBITO_DIRECTO, "debito.txt",
                archivo(detalleDebito("37", "0000000000012345", "000000000000123", ""),
                        detalleDebito("36", "0000000000005000", "000000000000124", "")),
                null, false));

        assertThat(resultado.getRegistros()).isEqualTo(2);
        assertThat(resultado.getImportados()).isEqualTo(1);
        assertThat(resultado.getRechazados()).isEqualTo(1);
        assertThat(resultado.getTotal()).isEqualByComparingTo(new BigDecimal("123.45"));

        ArgumentCaptor<ChequeraPago> pago = ArgumentCaptor.forClass(ChequeraPago.class);
        verify(chequeraPagoService).create(pago.capture());
        assertThat(pago.getValue().getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(pago.getValue().getTipoPagoId()).isEqualTo(5);

        verify(debitoCobranzaRepository).marcarArchivoBanco(eq(123L), any());
        verify(debitoCobranzaRepository).marcarRechazado(eq(124L), eq("RECHAZO MOTIVO DESCONOCIDO"), any());
        assertThat(filasGuardadas).hasSize(4);
        assertThat(filasGuardadas.get(0).getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(filasGuardadas.get(2).getImporte()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(filasGuardadas).allMatch(fila -> fila.getPasado() == 1);
    }

    @Test
    void visaBajaElImporteDeLaCuotaCuandoElBancoInformaMenos() {
        when(debitoCobranzaRepository.findByCuota(eq(6), eq(2), eq(123L), eq(1), eq(1), eq(1),
                eq(1))).thenReturn(Optional.of(debito(55L, 1)));

        ResultadoImportacion resultado = service.importar(SolicitudImportacion.deArchivo(
                CanalImportacion.DEBITO_VISA, "visa.txt",
                archivo(detalleVisa("00", "", "00", "", "000000000010000")), null, false));

        assertThat(resultado.getImportados()).isEqualTo(1);
        assertThat(resultado.getTotal()).isEqualByComparingTo(new BigDecimal("100.00"));

        verify(chequeraCuotaService, org.mockito.Mockito.atLeastOnce()).update(
                org.mockito.ArgumentMatchers.argThat(
                        cuota -> cuota.getImporte1().compareTo(new BigDecimal("100.00")) == 0), any());
        verify(debitoCobranzaRepository).marcarArchivoBanco(eq(55L), any());
        assertThat(filasGuardadas.get(0).getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void visaRechazadoNoCreaPagoYMarcaElDebito() {
        when(debitoCobranzaRepository.findByCuota(eq(6), eq(2), eq(123L), eq(1), eq(1), eq(1),
                eq(1))).thenReturn(Optional.of(debito(55L, 1)));

        ResultadoImportacion resultado = service.importar(SolicitudImportacion.deArchivo(
                CanalImportacion.DEBITO_VISA, "visa.txt",
                archivo(detalleVisa("01", "TARJETA VENCIDA", "00", "", "000000000010000")), null, false));

        assertThat(resultado.getImportados()).isZero();
        assertThat(resultado.getRechazados()).isEqualTo(1);
        verify(chequeraPagoService, org.mockito.Mockito.never()).create(any());
        verify(debitoCobranzaRepository).marcarRechazado(eq(55L), eq("TARJETA VENCIDA"), any());
    }

    private DebitoCobranza debito(Long debitoId, Integer cuotaId) {
        return DebitoCobranza.builder()
                .debitoId(debitoId)
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .productoId(1)
                .alternativaId(1)
                .cuotaId(cuotaId)
                .debitoTipoId(2)
                .rechazado((byte) 0)
                .build();
    }

    private byte[] archivo(String... lineas) {
        return (String.join("\n", lineas) + "\n").getBytes(StandardCharsets.ISO_8859_1);
    }

    private String detalleDebito(String codigoT, String importe, String debitoId, String motivo) {
        char[] registro = new char[601];
        Arrays.fill(registro, ' ');
        set(registro, 1, "3");
        set(registro, 22, codigoT);
        set(registro, 99, "20260315");
        set(registro, 152, importe);
        set(registro, 168, debitoId);
        set(registro, 209, motivo);
        return new String(registro);
    }

    private String detalleVisa(String codRechazo1, String descRechazo1, String codRechazo2,
                               String descRechazo2, String importe) {
        char[] registro = new char[300];
        Arrays.fill(registro, ' ');
        set(registro, 1, "1");
        set(registro, 43, "00010101");
        set(registro, 63, importe);
        set(registro, 95, "000000600200123");
        set(registro, 131, codRechazo1);
        set(registro, 133, descRechazo1);
        set(registro, 162, codRechazo2);
        set(registro, 164, descRechazo2);
        set(registro, 225, "150326");
        return new String(registro);
    }

    private void set(char[] registro, int posicion, String valor) {
        for (int i = 0; i < valor.length(); i++) {
            registro[posicion - 1 + i] = valor.charAt(i);
        }
    }

}
