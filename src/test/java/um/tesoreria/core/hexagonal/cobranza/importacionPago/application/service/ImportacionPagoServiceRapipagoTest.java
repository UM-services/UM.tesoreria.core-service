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
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoReacreditacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoBancoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoImportadoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.DebitoCobranzaRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ParserArchivoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ReemplazoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser.RapipagoFileParser;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportacionPagoServiceRapipagoTest {

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
    private final List<Byte> pasados = new ArrayList<>();

    @BeforeEach
    void setUp() {
        pasados.clear();
        List<ParserArchivoPago> parsers = List.of(new RapipagoFileParser());
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
            pasados.add(fila.getPasado());
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
                        .importe1(invocation.getArgument(5).equals(1)
                                ? new BigDecimal("123.45") : new BigDecimal("678.90"))
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
    void importaUnaLineaAcreditadaYOtraPendienteYDejaLaHuellaDeAmbas() {
        SolicitudImportacion solicitud = SolicitudImportacion.deArchivo(CanalImportacion.RAPIPAGO,
                "rapipago.txt", archivo(
                        grupo("01", "000000012345", "20260317", "A"),
                        grupo("02", "000000067890", "20260322", "P")).getBytes(StandardCharsets.ISO_8859_1),
                null, false);

        ResultadoImportacion resultado = service.importar(solicitud);

        assertThat(resultado.getRegistros()).isEqualTo(2);
        assertThat(resultado.getImportados()).isEqualTo(2);
        assertThat(resultado.getTotal()).isEqualByComparingTo(new BigDecimal("802.35"));

        ArgumentCaptor<ArchivoBanco> filas = ArgumentCaptor.forClass(ArchivoBanco.class);
        verify(archivoBancoRepository, org.mockito.Mockito.times(4)).save(filas.capture());
        assertThat(pasados).containsExactly((byte) 0, (byte) 1, (byte) 0, (byte) 1);
        assertThat(filas.getAllValues().get(0).getFechaAcreditacion())
                .isEqualTo(OffsetDateTime.parse("2026-03-17T00:00:00Z"));
        assertThat(filas.getAllValues().get(2).getFechaAcreditacion()).isNull();

        ArgumentCaptor<ChequeraPago> pagos = ArgumentCaptor.forClass(ChequeraPago.class);
        verify(chequeraPagoService, org.mockito.Mockito.times(2)).create(pagos.capture());
        assertThat(pagos.getAllValues().get(0).getAcreditacion())
                .isEqualTo(OffsetDateTime.parse("2026-03-17T00:00:00Z"));
        assertThat(pagos.getAllValues().get(0).getVerificador()).isEqualTo(7);
        assertThat(pagos.getAllValues().get(1).getAcreditacion()).isNull();
    }

    @Test
    void reacreditaElPagoExistenteSinCrearOtro() {
        String contenido = archivo(grupo("01", "000000012345", "20260317", "A"));
        when(archivoImportadoRepository.findById(17)).thenReturn(java.util.Optional.of(
                ArchivoImportado.builder().id(17).nombre("rapipago.txt").registros(1).build()));
        when(archivoBancoRepository.findAllByArchivo("rapipago.txt")).thenReturn(List.of(
                ArchivoBanco.builder()
                        .id(10L)
                        .codigoEmpresa("6")
                        .identificacion(200123)
                        .productoId(1)
                        .mesPago(3)
                        .anhoPago(2026)
                        .cuotaId(1)
                        .importe(new BigDecimal("123.45"))
                        .fechaPago(OffsetDateTime.parse("2026-03-15T00:00:00Z"))
                        .verificador("7")
                        .marcaAcreditacion("P")
                        .archivo("rapipago.txt")
                        .pasado((byte) 1)
                        .tipoPagoId(3)
                        .build()));
        ChequeraPago existente = ChequeraPago.builder()
                .chequeraPagoId(5L)
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .productoId(1)
                .alternativaId(1)
                .cuotaId(1)
                .fecha(OffsetDateTime.parse("2026-03-15T00:00:00Z"))
                .verificador(7)
                .importe(new BigDecimal("123.45"))
                .build();
        when(chequeraPagoService.findAllByCuota(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(existente));

        ResultadoReacreditacion resultado = service.reacreditar(17, SolicitudImportacion.deArchivo(
                CanalImportacion.RAPIPAGO, "rapipago.txt", contenido.getBytes(StandardCharsets.ISO_8859_1),
                null, false));

        assertThat(resultado.registros()).isEqualTo(1);
        assertThat(resultado.actualizados()).isEqualTo(1);
        assertThat(resultado.sinPago()).isZero();

        ArgumentCaptor<ChequeraPago> pago = ArgumentCaptor.forClass(ChequeraPago.class);
        verify(chequeraPagoService).update(pago.capture());
        assertThat(pago.getValue().getAcreditacion())
                .isEqualTo(OffsetDateTime.parse("2026-03-17T00:00:00Z"));
        assertThat(pago.getValue().getArchivoBancoIdAcreditacion()).isEqualTo(10L);

        ArgumentCaptor<ArchivoBanco> fila = ArgumentCaptor.forClass(ArchivoBanco.class);
        verify(archivoBancoRepository).save(fila.capture());
        assertThat(fila.getValue().getMarcaAcreditacion()).isEqualTo("A");
        assertThat(fila.getValue().getFechaAcreditacion())
                .isEqualTo(OffsetDateTime.parse("2026-03-17T00:00:00Z"));
        verify(chequeraPagoService, org.mockito.Mockito.never()).create(any());
    }

    private String archivo(String... grupos) {
        return String.join("\n", grupos) + "\n";
    }

    private String grupo(String cuota, String importe, String fechaAcreditacion, String marca) {
        return dato0(cuota, "20260315") + "\n" + dato1(importe, fechaAcreditacion, marca);
    }

    private String dato0(String cuota, String fechaPago) {
        char[] registro = registro(360);
        set(registro, 23, "D");
        set(registro, 43, "0");
        set(registro, 107, fechaPago);
        set(registro, 248, barra(cuota));
        return new String(registro);
    }

    private String dato1(String importe, String fechaAcreditacion, String marca) {
        char[] registro = registro(130);
        set(registro, 23, "D");
        set(registro, 43, "1");
        set(registro, 83, importe);
        set(registro, 98, fechaAcreditacion);
        set(registro, 106, marca);
        return new String(registro);
    }

    private String barra(String cuota) {
        char[] barra = registro(100);
        set(barra, 1, "123");
        set(barra, 4, "06");
        set(barra, 6, "002");
        set(barra, 9, "00123");
        set(barra, 14, "1");
        set(barra, 15, "03");
        set(barra, 17, "26");
        set(barra, 19, cuota);
        set(barra, 21, "01234");
        set(barra, 26, "15");
        set(barra, 28, "03");
        set(barra, 30, "2026");
        set(barra, 44, "7");
        return new String(barra);
    }

    private char[] registro(int length) {
        char[] registro = new char[length];
        Arrays.fill(registro, ' ');
        return registro;
    }

    private void set(char[] registro, int posicion, String valor) {
        for (int i = 0; i < valor.length(); i++) {
            registro[posicion - 1 + i] = valor.charAt(i);
        }
    }

}
