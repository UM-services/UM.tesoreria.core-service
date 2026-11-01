package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindByMercadoPagoContextIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.UpdateMercadoPagoContextUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.FindReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.UpdateReservaVacanteUseCase;
import um.tesoreria.core.service.facade.PagoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentEventUseCaseImplTest {

    private static final Long CONTEXTO_ID = 1L;
    private static final Long CUOTA_ID = 7L;
    private static final UUID RESERVA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String PAGO_ID = "12345";
    private static final OffsetDateTime APROBADO = OffsetDateTime.of(2026, 10, 21, 12, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime ACTUALIZADO = APROBADO.plusMinutes(5);

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

    @Test
    void unPagoAprobadoDeCuotaActualizaElContextoYRegistraElPago() {
        var context = contextoCuota();
        cuandoSeCargaYActualizaElContexto(context);

        useCase.processPaymentEvent(eventoCuota("approved"));

        assertThat(context.getIdMercadoPago()).isEqualTo(PAGO_ID);
        assertThat(context.getStatus()).isEqualTo("approved");
        assertThat(context.getImportePagado()).isEqualByComparingTo("1500.00");
        verify(pagoService).registraPagoMP(CONTEXTO_ID);
        verifyNoInteractions(findReservaVacanteUseCase);
    }

    @Test
    void unPagoNoAprobadoNoRegistraElPago() {
        var context = contextoCuota();
        cuandoSeCargaYActualizaElContexto(context);

        useCase.processPaymentEvent(eventoCuota("rejected"));

        assertThat(context.getStatus()).isEqualTo("rejected");
        verify(pagoService, never()).registraPagoMP(any());
    }

    /**
     * Un refund o chargeback del mismo paymentId es una transición legítima: el estado tiene que
     * persistirse y no se vuelve a registrar el pago. Cortar el evento completo por paymentId ya
     * procesado dejaría el contexto en "approved" para siempre.
     */
    @Test
    void unRefundDelMismoPagoActualizaElEstadoYNoVuelveARegistrarElPago() {
        var context = contextoCuota();
        context.setIdMercadoPago(PAGO_ID);
        context.setChequeraPagoId(99L);
        cuandoSeCargaYActualizaElContexto(context);

        useCase.processPaymentEvent(eventoCuota("refunded"));

        assertThat(context.getStatus()).isEqualTo("refunded");
        assertThat(context.getChequeraPagoId()).isEqualTo(99L);
        verify(pagoService, never()).registraPagoMP(any());
    }

    @Test
    void unMismatchDeCuotaNoAplicaNada() {
        var context = contextoCuota();
        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(context);
        var evento = PaymentProcessedEvent.builder()
                .mercadoPagoContextId(CONTEXTO_ID)
                .chequeraCuotaId(8L)
                .paymentId(PAGO_ID)
                .status("approved")
                .build();

        useCase.processPaymentEvent(evento);

        verify(updateMercadoPagoContextUseCase, never()).update(any(), any());
        verifyNoInteractions(pagoService);
    }

    @Test
    void unPagoAprobadoDeReservaMarcaLaReservaComoPagada() {
        var context = contextoReserva();
        cuandoSeCargaYActualizaElContexto(context);
        var reserva = ReservaVacante.builder().reservaVacanteId(RESERVA_ID).estado("pendiente").build();
        when(findReservaVacanteUseCase.findByReservaVacanteId(RESERVA_ID)).thenReturn(reserva);

        useCase.processPaymentEvent(eventoReserva("approved"));

        assertThat(reserva.getEstado()).isEqualTo("pagado");
        verify(updateReservaVacanteUseCase).update(reserva, RESERVA_ID);
        verifyNoInteractions(pagoService);
    }

    @Test
    void unPagoNoAprobadoDeReservaNoLaMarcaComoPagada() {
        var context = contextoReserva();
        cuandoSeCargaYActualizaElContexto(context);

        useCase.processPaymentEvent(eventoReserva("pending"));

        verifyNoInteractions(findReservaVacanteUseCase);
        verifyNoInteractions(updateReservaVacanteUseCase);
    }

    /**
     * Si el registro del pago falla, la excepción tiene que salir del caso de uso: la transacción
     * revierte y Kafka reintenta / deriva al DLT. Silenciarla dejaría el contexto actualizado sin
     * el pago.
     */
    @Test
    void unFalloAlRegistrarElPagoSePropaga() {
        var context = contextoCuota();
        cuandoSeCargaYActualizaElContexto(context);
        var error = new IllegalStateException("no se pudo insertar el pago");
        when(pagoService.registraPagoMP(CONTEXTO_ID)).thenThrow(error);

        assertThatThrownBy(() -> useCase.processPaymentEvent(eventoCuota("approved")))
                .isSameAs(error);
    }

    /**
     * Regresión del riesgo de eventos fuera de orden: Kafka ordena por `paymentId`, no por contexto.
     * Un evento con `dateLastUpdated` anterior al ya aplicado no puede revertir el estado.
     */
    @Test
    void unEventoMasViejoNoPisaElEstadoDelContexto() {
        var context = contextoCuota();
        context.setStatus("approved");
        context.setDateLastUpdated(ACTUALIZADO);
        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(context);

        useCase.processPaymentEvent(eventoCuota("refunded", APROBADO));

        assertThat(context.getStatus()).isEqualTo("approved");
        assertThat(context.getDateLastUpdated()).isEqualTo(ACTUALIZADO);
        verify(updateMercadoPagoContextUseCase, never()).update(any(), any());
        verifyNoInteractions(pagoService);
    }

    @Test
    void unEventoMasNuevoGuardaLaUltimaActualizacion() {
        var context = contextoCuota();
        context.setDateLastUpdated(APROBADO);
        cuandoSeCargaYActualizaElContexto(context);

        useCase.processPaymentEvent(eventoCuota("approved", ACTUALIZADO));

        assertThat(context.getDateLastUpdated()).isEqualTo(ACTUALIZADO);
        verify(pagoService).registraPagoMP(CONTEXTO_ID);
    }

    /**
     * Compatibilidad con el productor anterior a la fase 3: sin `dateLastUpdated` el evento se
     * aplica como siempre y no borra la marca ya guardada.
     */
    @Test
    void unEventoSinFechaDeActualizacionNoSeDescartaNiBorraLaMarca() {
        var context = contextoCuota();
        context.setDateLastUpdated(ACTUALIZADO);
        cuandoSeCargaYActualizaElContexto(context);

        useCase.processPaymentEvent(eventoCuota("approved"));

        assertThat(context.getDateLastUpdated()).isEqualTo(ACTUALIZADO);
        verify(pagoService).registraPagoMP(CONTEXTO_ID);
    }

    private void cuandoSeCargaYActualizaElContexto(MercadoPagoContext context) {
        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(CONTEXTO_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(), eq(CONTEXTO_ID)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private MercadoPagoContext contextoCuota() {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(CONTEXTO_ID)
                .chequeraCuotaId(CUOTA_ID)
                .build();
    }

    private MercadoPagoContext contextoReserva() {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(CONTEXTO_ID)
                .reservaVacanteId(RESERVA_ID)
                .build();
    }

    private PaymentProcessedEvent eventoCuota(String status) {
        return eventoCuota(status, null);
    }

    private PaymentProcessedEvent eventoCuota(String status, OffsetDateTime dateLastUpdated) {
        return PaymentProcessedEvent.builder()
                .mercadoPagoContextId(CONTEXTO_ID)
                .chequeraCuotaId(CUOTA_ID)
                .paymentId(PAGO_ID)
                .status(status)
                .transactionAmount(new BigDecimal("1500.00"))
                .dateApproved(APROBADO)
                .dateLastUpdated(dateLastUpdated)
                .source("WEBHOOK_V1")
                .build();
    }

    private PaymentProcessedEvent eventoReserva(String status) {
        return PaymentProcessedEvent.builder()
                .mercadoPagoContextId(CONTEXTO_ID)
                .reservaVacanteId(RESERVA_ID)
                .paymentId(PAGO_ID)
                .status(status)
                .transactionAmount(new BigDecimal("1500.00"))
                .dateApproved(APROBADO)
                .build();
    }

}
