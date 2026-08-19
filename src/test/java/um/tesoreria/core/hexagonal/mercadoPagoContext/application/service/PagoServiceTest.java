package um.tesoreria.core.hexagonal.mercadoPagoContext.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception.ChequeraPagoException;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.service.ChequeraPagoAsientoService;
import um.tesoreria.core.service.ChequeraPagoReemplazoService;
import um.tesoreria.core.service.facade.PagoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Cubre el comportamiento ACTUAL de PagoService (camino "approved") antes de
 * agregar el flujo de reversion de pago para los estados "rejected", "refunded",
 * "cancelled", "in_mediation" y "charged_back".
 *
 * Estos tests son la red de seguridad: si al agregar revertirPagoMP algo rompe
 * registraPagoMP/marcarPago, tienen que fallar aca.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

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

    private static final Integer FACULTAD_ID = 1;
    private static final Integer TIPO_CHEQUERA_ID = 2;
    private static final Long CHEQUERA_SERIE_ID = 3L;
    private static final Integer PRODUCTO_ID = 4;
    private static final Integer ALTERNATIVA_ID = 5;
    private static final Integer CUOTA_ID = 6;
    private static final Long CHEQUERA_CUOTA_ID = 100L;
    private static final Long MERCADO_PAGO_CONTEXT_ID = 200L;
    private static final String ID_MERCADO_PAGO = "MP-123456789";
    private static final byte PAGADO = 1;
    private static final byte NO_PAGADO = 0;
    private static final OffsetDateTime FECHA_PAGO_FIJA = OffsetDateTime.of(2026, 3, 1, 10, 0, 0, 0, ZoneOffset.UTC);

    private ChequeraCuota buildChequeraCuota() {
        return ChequeraCuota.builder()
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .productoId(PRODUCTO_ID)
                .alternativaId(ALTERNATIVA_ID)
                .cuotaId(CUOTA_ID)
                .mes(3)
                .anho(2026)
                .build();
    }

    private MercadoPagoContext buildMercadoPagoContext() {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .idMercadoPago(ID_MERCADO_PAGO)
                .importePagado(new BigDecimal("15000.00"))
                .fechaPago(FECHA_PAGO_FIJA)
                .fechaAcreditacion(FECHA_PAGO_FIJA)
                .build();
    }

    /**
     * Caso de borde: la cuota referenciada por el contexto de MP no existe
     * (dato inconsistente en la base). El método debe propagar la excepción
     * tal cual, sin intentar crear un pago, actualizar el contexto ni
     * recalcular nada — cortar ahí es más seguro que seguir con datos rotos.
     */
    @Test
    void registraPagoMP_whenChequeraCuotaNoExiste_propagaLaExcepcionYNoTocaNadaMas() {
        var context = buildMercadoPagoContext();

        when(mercadoPagoContextService.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(chequeraCuotaService.findByChequeraCuotaId(CHEQUERA_CUOTA_ID))
                .thenThrow(new ChequeraCuotaException(CHEQUERA_CUOTA_ID));

        assertThatThrownBy(() -> pagoService.registraPagoMP(MERCADO_PAGO_CONTEXT_ID))
                .isInstanceOf(ChequeraCuotaException.class);

        // Si la cuota no existe (dato inconsistente), no se debe intentar
        // crear un pago, actualizar el contexto ni recalcular nada.
        verify(chequeraPagoService, never()).findByIdMercadoPago(any());
        verify(chequeraPagoService, never()).create(any());
        verify(mercadoPagoContextService, never()).update(any(), any());
        verify(chequeraTotalService, never()).update(any(), any());
    }

    /**
     * Camino feliz: llega la primera notificación "approved" de MP para esta
     * cuota. Debe crear un único ChequeraPago, enlazarlo al contexto
     * (chequeraPagoId), marcar la cuota como pagada y recalcular el total de
     * la chequera. Esta es la red de seguridad principal: cualquier
     * regresión al agregar revertirPagoMP tiene que romper esto primero.
     */
    @Test
    void registraPagoMP_whenNoPreviousPago_createsChequeraPagoAndMarksCuotaAsPaid() {
        var context = buildMercadoPagoContext();
        var chequeraCuota = buildChequeraCuota();

        when(mercadoPagoContextService.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(chequeraCuotaService.findByChequeraCuotaId(CHEQUERA_CUOTA_ID)).thenReturn(chequeraCuota);
        // No existe pago previo para este idMercadoPago -> dispara el alta
        when(chequeraPagoService.findByIdMercadoPago(ID_MERCADO_PAGO)).thenThrow(new ChequeraPagoException(ID_MERCADO_PAGO));
        when(chequeraPagoService.nextOrden(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(1);

        var createdChequeraPago = ChequeraPago.builder()
                .chequeraPagoId(999L)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .idMercadoPago(ID_MERCADO_PAGO)
                .importe(context.getImportePagado())
                .build();
        when(chequeraPagoService.create(any(ChequeraPago.class))).thenReturn(createdChequeraPago);

        when(mercadoPagoContextService.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        // Colaboradores de marcarPago -> calcularPagado
        when(chequeraPagoService.isPagado(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(true);
        when(chequeraCuotaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(chequeraCuota);
        when(chequeraCuotaService.update(any(ChequeraCuota.class), eq(CHEQUERA_CUOTA_ID))).thenReturn(chequeraCuota);
        var chequeraTotal = ChequeraTotal.builder()
                .chequeraTotalId(500L)
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .productoId(PRODUCTO_ID)
                .build();
        when(chequeraTotalService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID))
                .thenReturn(chequeraTotal);
        when(chequeraCuotaService.calculateTotalCuotasActivas(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID))
                .thenReturn(new BigDecimal("30000.00"));
        when(chequeraCuotaService.calculateTotalCuotasPagadas(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID))
                .thenReturn(new BigDecimal("15000.00"));
        when(chequeraTotalService.update(any(ChequeraTotal.class), eq(500L))).thenReturn(chequeraTotal);

        var result = pagoService.registraPagoMP(MERCADO_PAGO_CONTEXT_ID);

        assertThat(result).isEqualTo(createdChequeraPago);

        // Se crea un solo ChequeraPago, nunca dos
        verify(chequeraPagoService, times(1)).create(any(ChequeraPago.class));

        // El contexto queda enlazado al pago creado
        var contextCaptorValue = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(mercadoPagoContextService).update(contextCaptorValue.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        assertThat(contextCaptorValue.getValue().getChequeraPagoId()).isEqualTo(createdChequeraPago.getChequeraPagoId());

        // La cuota queda marcada como pagada y se recalcula el total de la chequera
        var cuotaCaptor = ArgumentCaptor.forClass(ChequeraCuota.class);
        verify(chequeraCuotaService).update(cuotaCaptor.capture(), eq(CHEQUERA_CUOTA_ID));
        assertThat(cuotaCaptor.getValue().getPagado()).isEqualTo(PAGADO);

        verify(chequeraTotalService).update(any(ChequeraTotal.class), eq(500L));
    }

    /**
     * Idempotencia: MercadoPago puede reenviar la misma notificación de pago
     * más de una vez (reintentos, duplicados de webhook). Si ya existe un
     * ChequeraPago con ese idMercadoPago, debe devolverlo tal cual y NO
     * crear un pago nuevo ni volver a tocar contexto/cuota/total.
     */
    @Test
    void registraPagoMP_whenPagoYaExiste_esIdempotenteYNoDuplicaElPago() {
        var context = buildMercadoPagoContext();
        var chequeraCuota = buildChequeraCuota();
        var pagoExistente = ChequeraPago.builder()
                .chequeraPagoId(777L)
                .idMercadoPago(ID_MERCADO_PAGO)
                .build();

        when(mercadoPagoContextService.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(chequeraCuotaService.findByChequeraCuotaId(CHEQUERA_CUOTA_ID)).thenReturn(chequeraCuota);
        when(chequeraPagoService.findByIdMercadoPago(ID_MERCADO_PAGO)).thenReturn(pagoExistente);

        var result = pagoService.registraPagoMP(MERCADO_PAGO_CONTEXT_ID);

        assertThat(result).isEqualTo(pagoExistente);

        // Ante una notificacion de MP repetida, no se crea un pago nuevo ni se
        // vuelve a tocar el contexto/cuota/total
        verify(chequeraPagoService, never()).create(any());
        verify(mercadoPagoContextService, never()).update(any(), any());
        verify(chequeraCuotaService, never()).update(any(), any());
        verify(chequeraTotalService, never()).update(any(), any());
    }

    /**
     * Cuando la cuota queda completamente pagada (isPagado = true), marcarPago
     * debe: (1) marcar pagado=1 en la ChequeraCuota, y (2) recalcular el
     * ChequeraTotal de la chequera con los totales activos/pagados
     * agregados. Esto es lo que registraPagoMP delega al final.
     */
    @Test
    void marcarPago_whenCuotaCompletamentePagada_marcaPagadoYRecalculaTotal() {
        var chequeraCuota = buildChequeraCuota();

        when(chequeraPagoService.isPagado(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(true);
        when(chequeraCuotaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(chequeraCuota);
        when(chequeraCuotaService.update(any(ChequeraCuota.class), eq(CHEQUERA_CUOTA_ID))).thenReturn(chequeraCuota);

        var chequeraTotal = ChequeraTotal.builder()
                .chequeraTotalId(500L)
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .productoId(PRODUCTO_ID)
                .build();
        when(chequeraTotalService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID))
                .thenReturn(chequeraTotal);
        when(chequeraCuotaService.calculateTotalCuotasActivas(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID))
                .thenReturn(new BigDecimal("30000.00"));
        when(chequeraCuotaService.calculateTotalCuotasPagadas(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID))
                .thenReturn(new BigDecimal("30000.00"));
        when(chequeraTotalService.update(any(ChequeraTotal.class), eq(500L))).thenReturn(chequeraTotal);

        pagoService.marcarPago(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, chequeraCuotaService);

        var cuotaCaptor = ArgumentCaptor.forClass(ChequeraCuota.class);
        verify(chequeraCuotaService).update(cuotaCaptor.capture(), eq(CHEQUERA_CUOTA_ID));
        assertThat(cuotaCaptor.getValue().getPagado()).isEqualTo(PAGADO);

        var totalCaptor = ArgumentCaptor.forClass(ChequeraTotal.class);
        verify(chequeraTotalService).update(totalCaptor.capture(), eq(500L));
        assertThat(totalCaptor.getValue().getTotal()).isEqualByComparingTo("30000.00");
        assertThat(totalCaptor.getValue().getPagado()).isEqualByComparingTo("30000.00");
    }

    /**
     * Caso simétrico al anterior: si isPagado devuelve false (por ejemplo,
     * porque el pago recién se revirtió y ya no cubre toda la cuota),
     * marcarPago debe dejar pagado=0. Este assert es clave para el futuro
     * revertirPagoMP: cuando se revierta un pago, el flujo pasa por acá.
     */
    @Test
    void marcarPago_whenCuotaNoEstaPagada_marcaPagadoEnCero() {
        var chequeraCuota = buildChequeraCuota();

        when(chequeraPagoService.isPagado(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(false);
        when(chequeraCuotaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID))
                .thenReturn(chequeraCuota);
        when(chequeraCuotaService.update(any(ChequeraCuota.class), eq(CHEQUERA_CUOTA_ID))).thenReturn(chequeraCuota);

        var chequeraTotal = ChequeraTotal.builder()
                .chequeraTotalId(500L)
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .productoId(PRODUCTO_ID)
                .build();
        when(chequeraTotalService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID))
                .thenReturn(chequeraTotal);
        when(chequeraCuotaService.calculateTotalCuotasActivas(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID))
                .thenReturn(new BigDecimal("30000.00"));
        when(chequeraCuotaService.calculateTotalCuotasPagadas(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID))
                .thenReturn(BigDecimal.ZERO);
        when(chequeraTotalService.update(any(ChequeraTotal.class), eq(500L))).thenReturn(chequeraTotal);

        pagoService.marcarPago(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ID, ALTERNATIVA_ID, CUOTA_ID, chequeraCuotaService);

        var cuotaCaptor = ArgumentCaptor.forClass(ChequeraCuota.class);
        verify(chequeraCuotaService).update(cuotaCaptor.capture(), eq(CHEQUERA_CUOTA_ID));
        assertThat(cuotaCaptor.getValue().getPagado()).isEqualTo(NO_PAGADO);
    }
}