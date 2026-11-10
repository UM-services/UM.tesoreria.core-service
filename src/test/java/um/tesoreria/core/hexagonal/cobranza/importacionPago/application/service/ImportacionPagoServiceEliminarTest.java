package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception.ImportacionPagoException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoEliminacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoBancoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoImportadoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.DebitoCobranzaRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ParserArchivoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ReemplazoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportacionPagoServiceEliminarTest {

    private static final String ARCHIVO = "PMC_20260315.txt";

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

    @BeforeEach
    void setUp() {
        service = new ImportacionPagoService(List.<ParserArchivoPago>of(), archivoImportadoRepository,
                archivoBancoRepository, reemplazoRepository, payPerTicRendicionRepository,
                debitoCobranzaRepository, chequeraSerieService, chequeraCuotaService, chequeraPagoService);
        when(archivoImportadoRepository.findById(17)).thenReturn(Optional.of(cabecera()));
        when(chequeraPagoService.findAllByArchivo(ARCHIVO)).thenReturn(List.of(
                pago(1, 1), pago(1, 1), pago(2, 1)));
        when(reemplazoRepository.findAllPagosByArchivo(ARCHIVO)).thenReturn(List.of(
                reemplazoPago(3, 1)));
        when(archivoBancoRepository.findAllByArchivo(ARCHIVO)).thenReturn(List.of(
                ArchivoBanco.builder().id(1L).importe(new BigDecimal("100.00")).build(),
                ArchivoBanco.builder().id(2L).importe(new BigDecimal("50.00")).build()));
        when(reemplazoRepository.findCuotaByUnique(any(), any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());
    }

    @Test
    void eliminaPagosCabeceraYFilasDeBancoYRecalculaLasCuotas() {
        ResultadoEliminacion resultado = service.eliminar(17);

        assertThat(resultado.getFilename()).isEqualTo(ARCHIVO);
        assertThat(resultado.getPagosEliminados()).isEqualTo(3);
        assertThat(resultado.getPagosReemplazoEliminados()).isEqualTo(1);
        assertThat(resultado.getArchivosBancoEliminados()).isEqualTo(2);
        // dos cuotas normales distintas (producto 1 y 2) + una de reemplazo
        assertThat(resultado.getCuotasRecalculadas()).isEqualTo(3);

        verify(chequeraPagoService).deleteAllByArchivo(ARCHIVO);
        verify(reemplazoRepository).deletePagosByArchivo(ARCHIVO);
        verify(archivoBancoRepository).deleteByArchivo(ARCHIVO);
        verify(archivoImportadoRepository).delete(any(ArchivoImportado.class));
        verify(chequeraPagoService).marcarPago(6, 2, 123L, 1, 1, 1);
        verify(chequeraPagoService).marcarPago(6, 2, 123L, 2, 1, 1);
    }

    @Test
    void laSegundEliminacionNoEncuentraLaCabecera() {
        service.eliminar(17);
        when(archivoImportadoRepository.findById(17)).thenReturn(Optional.empty());
        clearInvocations(archivoImportadoRepository);

        assertThatThrownBy(() -> service.eliminar(17))
                .isInstanceOf(ImportacionPagoException.class)
                .hasMessageContaining("No existe la importación 17");
        verify(archivoImportadoRepository, never()).delete(any(ArchivoImportado.class));
    }

    @Test
    void noTocaDebitosPayPerTicNiElImporteDeLaCuota() {
        service.eliminar(17);

        // Paridad con el legacy: el "Eliminar" no revierte débitos, PayPerTIC ni importe1.
        verifyNoInteractions(debitoCobranzaRepository);
        verifyNoInteractions(payPerTicRendicionRepository);
        verifyNoInteractions(chequeraCuotaService);
    }

    @Test
    void eliminarCorreEnUnaSolaTransaccion() throws NoSuchMethodException {
        assertThat(ImportacionPagoService.class.getMethod("eliminar", Integer.class)
                .isAnnotationPresent(Transactional.class)).isTrue();
    }

    private ArchivoImportado cabecera() {
        return ArchivoImportado.builder().id(17).nombre(ARCHIVO).registros(3).build();
    }

    private ChequeraPago pago(Integer productoId, Integer cuotaId) {
        return ChequeraPago.builder()
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .productoId(productoId)
                .alternativaId(1)
                .cuotaId(cuotaId)
                .importe(new BigDecimal("100.00"))
                .archivo(ARCHIVO)
                .build();
    }

    private ReemplazoPago reemplazoPago(Integer productoId, Integer cuotaId) {
        return ReemplazoPago.builder()
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .productoId(productoId)
                .alternativaId(1)
                .cuotaId(cuotaId)
                .importe(new BigDecimal("80.00"))
                .archivo(ARCHIVO)
                .build();
    }

}
