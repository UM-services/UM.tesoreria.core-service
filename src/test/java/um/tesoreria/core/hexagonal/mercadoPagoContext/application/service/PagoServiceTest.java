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
import um.tesoreria.core.model.FacturacionElectronica;
import um.tesoreria.core.service.ChequeraPagoAsientoService;
import um.tesoreria.core.service.ChequeraPagoReemplazoService;
import um.tesoreria.core.service.FacturacionElectronicaService;
import um.tesoreria.core.service.facade.PagoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Cubre el comportamiento de PagoService: el alta de pago ("approved", vía
 * registraPagoMP) y la reversión de pago (estados "rejected", "refunded",
 * "cancelled", "in_mediation", "charged_back", vía revertirPagoMP), incluyendo
 * la idempotencia de ambos frente a notificaciones repetidas de MercadoPago.
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
    @Mock
    private FacturacionElectronicaService facturacionElectronicaService;

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
     * Camino feliz de la reversión: llega un estado como "rejected" o
     * "refunded" para un pago que SÍ está registrado. Debe: (1) borrar el
     * asiento contable asociado, (2) borrar el ChequeraPago existente,
     * (3) limpiar chequeraPagoId/importePagado/fechaPago/fechaAcreditacion
     * del contexto (para que no queden mostrando datos de un pago que ya no
     * existe), y (4) volver a llamar marcarPago para que la cuota quede en
     * pagado=0 y el total de la chequera se recalcule sin ese pago.
     */
    @Test
    void revertirPagoMP_whenPagoExiste_loEliminaYRecalculaPagado() {
        var context = buildMercadoPagoContext();
        var chequeraCuota = buildChequeraCuota();
        var pagoExistente = ChequeraPago.builder()
                .chequeraPagoId(999L)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .idMercadoPago(ID_MERCADO_PAGO)
                .build();

        when(mercadoPagoContextService.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(chequeraCuotaService.findByChequeraCuotaId(CHEQUERA_CUOTA_ID)).thenReturn(chequeraCuota);
        when(chequeraPagoService.findByIdMercadoPago(ID_MERCADO_PAGO)).thenReturn(pagoExistente);
        when(facturacionElectronicaService.findAllByChequeraPagoIds(List.of(999L))).thenReturn(List.of());
        when(mercadoPagoContextService.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        // Colaboradores de marcarPago -> calcularPagado. Tras revertir, la
        // cuota ya no está cubierta por ningún pago confirmado.
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

        pagoService.revertirPagoMP(MERCADO_PAGO_CONTEXT_ID);

        verify(chequeraPagoAsientoService).deleteAllByChequeraPagoId(999L);
        verify(chequeraPagoService).deleteByChequeraPagoId(999L);

        var contextCaptor = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(mercadoPagoContextService).update(contextCaptor.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        assertThat(contextCaptor.getValue().getChequeraPagoId()).isNull();
        assertThat(contextCaptor.getValue().getImportePagado()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(contextCaptor.getValue().getFechaPago()).isNull();
        assertThat(contextCaptor.getValue().getFechaAcreditacion()).isNull();

        var cuotaCaptor = ArgumentCaptor.forClass(ChequeraCuota.class);
        verify(chequeraCuotaService).update(cuotaCaptor.capture(), eq(CHEQUERA_CUOTA_ID));
        assertThat(cuotaCaptor.getValue().getPagado()).isEqualTo(NO_PAGADO);

        verify(chequeraTotalService).update(any(ChequeraTotal.class), eq(500L));
    }

    /**
     * Si el pago ya tiene una factura electrónica generada, ARCA se hace
     * cargo del aspecto fiscal por su cuenta — la factura NO se toca ni se
     * borra. Lo que sí hace revertirPagoMP es desvincularla del pago
     * (chequeraPagoId → null y chequeraPago → null en la factura, ambos:
     * el escalar y la referencia de objeto, porque dejar la segunda
     * colgando hace que Hibernate tire TransientPropertyValueException al
     * borrar el ChequeraPago en la misma transacción), porque hay un FK
     * real en la base que impediría borrar el ChequeraPago mientras la
     * factura lo siga referenciando. Después de desvincular, sigue con el
     * resto del flujo normal (borra asiento, borra pago, limpia contexto,
     * recalcula la cuota) exactamente igual que si nunca hubiera existido
     * factura.
     */
    @Test
    void revertirPagoMP_whenPagoYaTieneFacturaElectronica_laDesvinculaYRevierteIgual() {
        var context = buildMercadoPagoContext();
        var chequeraCuota = buildChequeraCuota();
        var pagoExistente = ChequeraPago.builder()
                .chequeraPagoId(999L)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .idMercadoPago(ID_MERCADO_PAGO)
                .build();
        var facturaExistente = FacturacionElectronica.builder()
                .facturacionElectronicaId(555L)
                .chequeraPagoId(999L)
                .build();

        when(mercadoPagoContextService.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(chequeraCuotaService.findByChequeraCuotaId(CHEQUERA_CUOTA_ID)).thenReturn(chequeraCuota);
        when(chequeraPagoService.findByIdMercadoPago(ID_MERCADO_PAGO)).thenReturn(pagoExistente);
        when(facturacionElectronicaService.findAllByChequeraPagoIds(List.of(999L))).thenReturn(List.of(facturaExistente));
        when(facturacionElectronicaService.update(any(FacturacionElectronica.class), eq(555L))).thenReturn(facturaExistente);
        when(mercadoPagoContextService.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

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

        pagoService.revertirPagoMP(MERCADO_PAGO_CONTEXT_ID);

        // La factura se desvincula (chequeraPagoId -> null, chequeraPago -> null) pero NO se borra
        var facturaCaptor = ArgumentCaptor.forClass(FacturacionElectronica.class);
        verify(facturacionElectronicaService).update(facturaCaptor.capture(), eq(555L));
        assertThat(facturaCaptor.getValue().getChequeraPagoId()).isNull();
        assertThat(facturaCaptor.getValue().getChequeraPago()).isNull();

        // Y el resto del flujo sigue normal, como si nunca hubiera habido factura
        verify(chequeraPagoAsientoService).deleteAllByChequeraPagoId(999L);
        verify(chequeraPagoService).deleteByChequeraPagoId(999L);
        verify(mercadoPagoContextService).update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID));
        verify(chequeraCuotaService).update(any(ChequeraCuota.class), eq(CHEQUERA_CUOTA_ID));
        verify(chequeraTotalService).update(any(ChequeraTotal.class), eq(500L));
    }

    /**
     * Idempotencia simétrica a registraPagoMP: si no hay ningún ChequeraPago
     * para ese idMercadoPago (nunca se registró, o ya se revirtió antes),
     * revertirPagoMP debe ser un no-op — no borrar nada, no tocar el
     * contexto, no recalcular nada.
     */
    @Test
    void revertirPagoMP_whenNoExistePago_esIdempotenteYNoHaceNada() {
        var context = buildMercadoPagoContext();
        var chequeraCuota = buildChequeraCuota();

        when(mercadoPagoContextService.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(chequeraCuotaService.findByChequeraCuotaId(CHEQUERA_CUOTA_ID)).thenReturn(chequeraCuota);
        when(chequeraPagoService.findByIdMercadoPago(ID_MERCADO_PAGO)).thenThrow(new ChequeraPagoException(ID_MERCADO_PAGO));

        pagoService.revertirPagoMP(MERCADO_PAGO_CONTEXT_ID);

        verify(chequeraPagoAsientoService, never()).deleteAllByChequeraPagoId(any());
        verify(chequeraPagoService, never()).deleteByChequeraPagoId(any());
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
     * marcarPago debe dejar pagado=0. Este es el mismo camino que ejercita
     * revertirPagoMP al final de su flujo.
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