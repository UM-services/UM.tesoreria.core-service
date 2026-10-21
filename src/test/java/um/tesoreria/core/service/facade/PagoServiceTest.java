package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception.ChequeraPagoException;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.service.ChequeraPagoAsientoService;
import um.tesoreria.core.service.ChequeraPagoReemplazoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Idempotencia de {@link PagoService#registraPagoMP(Long)}: el mismo pago de Mercado Pago no puede
 * generar dos filas en {@code chequera_pago}; un pago distinto sobre una cuota ya pagada sí se
 * registra (doble pago real, se resuelve por el circuito administrativo).
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private static final Long CONTEXTO_ID = 1L;
    private static final Long CUOTA_ID = 7L;
    private static final String PAGO_MP = "12345";
    private static final OffsetDateTime FECHA_PAGO = OffsetDateTime.of(2026, 10, 21, 12, 0, 0, 0, ZoneOffset.UTC);

    @Mock
    private ChequeraPagoService chequeraPagoService;
    @Mock
    private ChequeraPagoReemplazoService chequeraPagoReemplazoService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;
    @Mock
    private ChequeraTotalService chequeraTotalService;
    @Mock
    private MercadoPagoContextService mercadoPagoContextService;
    @Mock
    private ChequeraPagoAsientoService chequeraPagoAsientoService;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void elMismoPagoYaRegistradoEsUnNoOp() {
        when(mercadoPagoContextService.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(contexto(PAGO_MP));
        var existente = ChequeraPago.builder().chequeraPagoId(99L).idMercadoPago(PAGO_MP).build();
        when(chequeraPagoService.findByIdMercadoPago(PAGO_MP)).thenReturn(existente);

        var resultado = pagoService.registraPagoMP(CONTEXTO_ID);

        assertThat(resultado.getChequeraPagoId()).isEqualTo(99L);
        verify(chequeraPagoService, never()).create(any());
        verify(mercadoPagoContextService, never()).update(any(), any());
    }

    @Test
    void unPagoNuevoSeRegistraConElProximoOrden() {
        stubCaminoFeliz((byte) 0);
        var captor = ArgumentCaptor.forClass(ChequeraPago.class);

        var resultado = pagoService.registraPagoMP(CONTEXTO_ID);

        verify(chequeraPagoService).create(captor.capture());
        var creado = captor.getValue();
        assertThat(creado.getOrden()).isEqualTo(3);
        assertThat(creado.getTipoPagoId()).isEqualTo(18);
        assertThat(creado.getIdMercadoPago()).isEqualTo(PAGO_MP);
        assertThat(creado.getChequeraCuotaId()).isEqualTo(CUOTA_ID);
        assertThat(creado.getImporte()).isEqualByComparingTo("1500.00");
        assertThat(resultado.getChequeraPagoId()).isEqualTo(99L);
        verify(mercadoPagoContextService).update(any(), eq(CONTEXTO_ID));
        verify(chequeraCuotaService).update(any(), eq(CUOTA_ID));
    }

    /**
     * Un pago distinto sobre una cuota ya pagada es un doble pago real: se registra igual (con
     * WARN en el log) para que el circuito administrativo lo resuelva por reversa.
     */
    @Test
    void unPagoDistintoSobreUnaCuotaYaPagadaSeRegistraIgual() {
        stubCaminoFeliz((byte) 1);

        pagoService.registraPagoMP(CONTEXTO_ID);

        verify(chequeraPagoService).create(any());
    }

    /**
     * El conflicto de unicidad no se captura dentro de la transacción (quedaría rollback-only): se
     * propaga para que Kafka reintente. En el reintento el pre-check encuentra el pago ganador.
     */
    @Test
    void elConflictoDeUnicidadSePropagaParaQueKafkaReintente() {
        when(mercadoPagoContextService.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(contexto(PAGO_MP));
        doThrow(new ChequeraPagoException(PAGO_MP)).when(chequeraPagoService).findByIdMercadoPago(PAGO_MP);
        when(chequeraCuotaService.findByChequeraCuotaId(CUOTA_ID)).thenReturn(cuota((byte) 0));
        when(chequeraPagoService.nextOrden(anyInt(), anyInt(), any(), anyInt(), anyInt(), anyInt())).thenReturn(3);
        doThrow(new DataIntegrityViolationException("Duplicate entry '12345' for key 'uk_chequera_pago_id_mp'"))
                .when(chequeraPagoService).create(any());

        assertThatThrownBy(() -> pagoService.registraPagoMP(CONTEXTO_ID))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(mercadoPagoContextService, never()).update(any(), any());
    }

    @Test
    void unContextoSinIdMercadoPagoFallaYNoCreaNada() {
        when(mercadoPagoContextService.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(contexto(null));

        assertThatThrownBy(() -> pagoService.registraPagoMP(CONTEXTO_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sin idMercadoPago");

        verify(chequeraPagoService, never()).create(any());
    }

    private void stubCaminoFeliz(byte pagado) {
        when(mercadoPagoContextService.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(contexto(PAGO_MP));
        doThrow(new ChequeraPagoException(PAGO_MP)).when(chequeraPagoService).findByIdMercadoPago(PAGO_MP);
        when(chequeraCuotaService.findByChequeraCuotaId(CUOTA_ID)).thenReturn(cuota(pagado));
        when(chequeraPagoService.nextOrden(anyInt(), anyInt(), any(), anyInt(), anyInt(), anyInt())).thenReturn(3);
        when(chequeraPagoService.create(any())).thenReturn(ChequeraPago.builder().chequeraPagoId(99L).build());
        when(mercadoPagoContextService.update(any(), eq(CONTEXTO_ID))).thenReturn(contexto(PAGO_MP));
        when(chequeraPagoService.isPagado(anyInt(), anyInt(), any(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(chequeraCuotaService.findByUnique(anyInt(), anyInt(), any(), anyInt(), anyInt(), anyInt()))
                .thenReturn(cuota(pagado));
        when(chequeraCuotaService.update(any(), eq(CUOTA_ID))).thenReturn(cuota(pagado));
        when(chequeraCuotaService.calculateTotalCuotasActivas(anyInt(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(new BigDecimal("1500.00"));
        when(chequeraCuotaService.calculateTotalCuotasPagadas(anyInt(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(new BigDecimal("1500.00"));
        when(chequeraTotalService.findByUnique(anyInt(), anyInt(), any(), anyInt())).thenReturn(ChequeraTotal.builder()
                .chequeraTotalId(3L)
                .facultadId(1)
                .tipoChequeraId(2)
                .chequeraSerieId(100L)
                .productoId(4)
                .total(BigDecimal.ZERO)
                .pagado(BigDecimal.ZERO)
                .build());
        when(chequeraTotalService.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private MercadoPagoContext contexto(String idMercadoPago) {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(CONTEXTO_ID)
                .chequeraCuotaId(CUOTA_ID)
                .idMercadoPago(idMercadoPago)
                .fechaPago(FECHA_PAGO)
                .fechaAcreditacion(FECHA_PAGO)
                .importePagado(new BigDecimal("1500.00"))
                .status("approved")
                .build();
    }

    private ChequeraCuota cuota(byte pagado) {
        return ChequeraCuota.builder()
                .chequeraCuotaId(CUOTA_ID)
                .facultadId(1)
                .tipoChequeraId(2)
                .chequeraSerieId(100L)
                .productoId(4)
                .alternativaId(1)
                .cuotaId(5)
                .mes(10)
                .anho(2026)
                .pagado(pagado)
                .build();
    }

}
