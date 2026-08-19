package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindByMercadoPagoContextIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.UpdateMercadoPagoContextUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.FindReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.UpdateReservaVacanteUseCase;
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
 * Cubre el comportamiento ACTUAL de ProcessPaymentEventUseCaseImpl: hoy solo
 * reacciona al estado "approved". Los tests marcados como "hoy no revierte" (con
 * status rejected/refunded/cancelled/charged_back/in_mediation) documentan el
 * hueco que hay que cerrar: son la red de seguridad, y esos justamente son los
 * que van a cambiar de comportamiento (y por lo tanto de aserciones) cuando se
 * agregue revertirPagoMP.
 */
@ExtendWith(MockitoExtension.class)
class ProcessPaymentEventUseCaseImplTest {

    @Mock
    private FindByMercadoPagoContextIdUseCase findByMercadoPagoContextIdUseCase;
    @Mock
    private UpdateMercadoPagoContextUseCase updateMercadoPagoContextUseCase;
    @Mock
    private PagoService pagoService;
    @Mock
    private FindReservaVacanteUseCase findReservaVacanteUseCase;
    @Mock
    private UpdateReservaVacanteUseCase updateReservaVacanteUseCase;

    @InjectMocks
    private ProcessPaymentEventUseCaseImpl useCase;

    private static final Long MERCADO_PAGO_CONTEXT_ID = 1L;
    private static final Long CHEQUERA_CUOTA_ID = 100L;
    private static final OffsetDateTime FECHA_APROBACION_FIJA = OffsetDateTime.of(2026, 3, 1, 10, 0, 0, 0, ZoneOffset.UTC);

    private PaymentProcessedEvent.PaymentProcessedEventBuilder eventForCuota(String status) {
        return PaymentProcessedEvent.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .paymentId("MP-1")
                .status(status)
                .dateApproved(FECHA_APROBACION_FIJA)
                .transactionAmount(new BigDecimal("15000.00"))
                .paymentJson("{}");
    }

    // ---------- Pago de cuota ----------

    /**
     * Camino feliz de cuota: llega un evento "approved" para un contexto que
     * coincide en chequeraCuotaId. Debe: (1) volcar los datos del evento
     * (status, idMercadoPago, importe, fecha) al contexto y persistirlo, y
     * (2) delegar en PagoService.registraPagoMP para dar de alta el pago.
     */
    @Test
    void cuota_approved_registraElPago() {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .build();
        var event = eventForCuota("approved").build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        useCase.processPaymentEvent(event);

        // El contexto se actualiza con los datos del evento antes de delegar en PagoService
        var contextCaptor = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(updateMercadoPagoContextUseCase).update(contextCaptor.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        var contextoActualizado = contextCaptor.getValue();
        assertThat(contextoActualizado.getStatus()).isEqualTo("approved");
        assertThat(contextoActualizado.getIdMercadoPago()).isEqualTo(event.getPaymentId());
        assertThat(contextoActualizado.getImportePagado()).isEqualByComparingTo(event.getTransactionAmount());
        assertThat(contextoActualizado.getFechaPago()).isEqualTo(event.getDateApproved());

        verify(pagoService, times(1)).registraPagoMP(MERCADO_PAGO_CONTEXT_ID);
    }

    /**
     * EL HUECO ACTUAL: para cada uno de los 5 estados nuevos, el contexto SÍ
     * se actualiza con el status recibido, pero PagoService no se toca para
     * nada (no se revierte el pago). Cuando se implemente revertirPagoMP,
     * este test es el que hay que reescribir (cambiar verifyNoInteractions
     * por verify(pagoService).revertirPagoMP(...)) para que siga siendo la
     * red de seguridad correcta.
     */
    @ParameterizedTest
    @ValueSource(strings = {"rejected", "refunded", "cancelled", "in_mediation", "charged_back"})
    void cuota_estadoNoApproved_hoyActualizaElContextoPeroNoRevierteElPago(String status) {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .build();
        var event = eventForCuota(status).build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        useCase.processPaymentEvent(event);

        // El contexto SI se actualiza con el nuevo estado...
        var contextCaptor = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(updateMercadoPagoContextUseCase).update(contextCaptor.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        assertThat(contextCaptor.getValue().getStatus()).isEqualTo(status);

        // ...pero HOY no se revierte el pago de la cuota. Este assert es el que
        // va a cambiar (a verify(pagoService).revertirPagoMP(...)) una vez que
        // se implemente la reversion.
        verifyNoInteractions(pagoService);
    }

    /**
     * Guarda de integridad: si el chequeraCuotaId del evento no coincide con
     * el del contexto guardado (evento desincronizado o corrupto), el use
     * case debe cortar temprano — no actualizar el contexto ni delegar en
     * PagoService — en vez de aplicar datos de una cuota equivocada.
     */
    @Test
    void cuota_mismatchChequeraCuotaId_noActualizaNiRegistraNada() {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .build();
        var event = eventForCuota("approved").chequeraCuotaId(999L).build(); // no coincide con el contexto

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);

        useCase.processPaymentEvent(event);

        verifyNoInteractions(updateMercadoPagoContextUseCase);
        verifyNoInteractions(pagoService);
    }

    // ---------- Contexto inexistente ----------

    /**
     * Caso de borde: llega un evento para un mercadoPagoContextId que no
     * existe en la base (dato inconsistente o carrera entre servicios). El
     * puerto findByMercadoPagoContextIdUseCase tira MercadoPagoContextException
     * y esa excepción debe propagarse tal cual, sin tocar ningún otro
     * colaborador (ni el update del contexto, ni PagoService, ni reserva de
     * vacante).
     */
    @Test
    void contextoInexistente_propagaLaExcepcionYNoTocaNadaMas() {
        var event = eventForCuota("approved").build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID))
                .thenThrow(new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId", MERCADO_PAGO_CONTEXT_ID));

        assertThatThrownBy(() -> useCase.processPaymentEvent(event))
                .isInstanceOf(MercadoPagoContextException.class);

        verifyNoInteractions(updateMercadoPagoContextUseCase);
        verifyNoInteractions(pagoService);
        verifyNoInteractions(findReservaVacanteUseCase);
        verifyNoInteractions(updateReservaVacanteUseCase);
    }
}