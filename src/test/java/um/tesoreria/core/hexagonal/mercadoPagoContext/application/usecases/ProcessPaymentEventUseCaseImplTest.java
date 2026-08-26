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
import static org.mockito.Mockito.*;

/**
 * Cubre el comportamiento de ProcessPaymentEventUseCaseImpl: reacciona al
 * estado "approved" (da de alta el pago vía PagoService.registraPagoMP) y a
 * los estados "rejected", "refunded" y "charged_back" (deshace el pago vía
 * PagoService.revertirPagoMP) — alcance acotado a pedido explícito del
 * equipo. "cancelled" e "in_mediation" quedan documentados como pendientes
 * de habilitar. Solo cubre la rama de cuota; la rama de reserva de vacante
 * no forma parte del alcance de la reversión.
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
    private static final UUID RESERVA_VACANTE_ID = UUID.randomUUID();
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

    private PaymentProcessedEvent.PaymentProcessedEventBuilder eventForReserva(String status) {
        return PaymentProcessedEvent.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .reservaVacanteId(RESERVA_VACANTE_ID)
                .paymentId("MP-2")
                .status(status)
                .dateApproved(FECHA_APROBACION_FIJA)
                .transactionAmount(new BigDecimal("50000.00"))
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
     * Estados habilitados hoy (a pedido explícito del equipo): "rejected"
     * (nunca se cobró), "refunded" y "charged_back" (se cobró y la plata
     * volvió al alumno). Para los tres, el contexto se actualiza con el
     * status recibido Y se delega en PagoService.revertirPagoMP para
     * deshacer el pago de la cuota.
     */
    @ParameterizedTest
    @ValueSource(strings = {"rejected", "refunded", "charged_back"})
    void cuota_estadoHabilitado_revierteElPago(String status) {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .build();
        var event = eventForCuota(status).build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        useCase.processPaymentEvent(event);

        var contextCaptor = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(updateMercadoPagoContextUseCase).update(contextCaptor.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        assertThat(contextCaptor.getValue().getStatus()).isEqualTo(status);

        verify(pagoService, times(1)).revertirPagoMP(MERCADO_PAGO_CONTEXT_ID);
        verify(pagoService, never()).registraPagoMP(any());
    }

    /**
     * LOS 2 ESTADOS QUE TODAVÍA NO ESTÁN HABILITADOS: "cancelled" e
     * "in_mediation" siguen fuera del alcance actual (el equipo pidió
     * arrancar con rejected/refunded/charged_back). El contexto SÍ se
     * actualiza con el status recibido, pero PagoService no se toca todavía.
     * Cuando se habiliten, este test es el que hay que achicar (sacando el
     * estado que se habilite) y sumarlo al parametrizado de arriba.
     */
    @ParameterizedTest
    @ValueSource(strings = {"cancelled", "in_mediation"})
    void cuota_estadosAunNoHabilitados_actualizaContextoPeroNoRevierte(String status) {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .chequeraCuotaId(CHEQUERA_CUOTA_ID)
                .build();
        var event = eventForCuota(status).build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        useCase.processPaymentEvent(event);

        var contextCaptor = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(updateMercadoPagoContextUseCase).update(contextCaptor.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        assertThat(contextCaptor.getValue().getStatus()).isEqualTo(status);

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

    // ---------- Pago de reserva de vacante ----------

    /**
     * Camino feliz de reserva de vacante: llega un evento "approved" para un
     * contexto que coincide en reservaVacanteId. Debe: (1) volcar los datos
     * del evento al contexto y persistirlo (igual que en cuota), y
     * (2) marcar la ReservaVacante como "pagado". A diferencia de la rama de
     * cuota, acá no interviene PagoService — se actualiza directo vía
     * updateReservaVacanteUseCase.
     */
    @Test
    void reservaVacante_approved_marcaLaReservaComoPagada() {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .reservaVacanteId(RESERVA_VACANTE_ID)
                .build();
        var event = eventForReserva("approved").build();
        var reservaVacante = ReservaVacante.builder()
                .reservaVacanteId(RESERVA_VACANTE_ID)
                .estado("pendiente")
                .build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);
        when(findReservaVacanteUseCase.findByReservaVacanteId(RESERVA_VACANTE_ID)).thenReturn(reservaVacante);

        useCase.processPaymentEvent(event);

        var contextCaptor = ArgumentCaptor.forClass(MercadoPagoContext.class);
        verify(updateMercadoPagoContextUseCase).update(contextCaptor.capture(), eq(MERCADO_PAGO_CONTEXT_ID));
        assertThat(contextCaptor.getValue().getStatus()).isEqualTo("approved");
        assertThat(contextCaptor.getValue().getIdMercadoPago()).isEqualTo(event.getPaymentId());

        var reservaCaptor = ArgumentCaptor.forClass(ReservaVacante.class);
        verify(updateReservaVacanteUseCase).update(reservaCaptor.capture(), eq(RESERVA_VACANTE_ID));
        assertThat(reservaCaptor.getValue().getEstado()).isEqualTo("pagado");
    }

    /**
     * EL MISMO HUECO QUE EN CUOTA, pero del lado de reserva de vacante: para
     * los 5 estados nuevos, hoy no se toca la ReservaVacante para nada (ni se
     * consulta ni se actualiza). Esto está fuera del alcance actual (solo
     * cuotas), pero queda documentado como comportamiento existente por si
     * en el futuro el equipo decide extender la reversión también acá.
     */
    @ParameterizedTest
    @ValueSource(strings = {"rejected", "refunded", "cancelled", "in_mediation", "charged_back"})
    void reservaVacante_estadoNoApproved_hoyNoTocaLaReserva(String status) {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .reservaVacanteId(RESERVA_VACANTE_ID)
                .build();
        var event = eventForReserva(status).build();

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);
        when(updateMercadoPagoContextUseCase.update(any(MercadoPagoContext.class), eq(MERCADO_PAGO_CONTEXT_ID))).thenReturn(context);

        useCase.processPaymentEvent(event);

        verifyNoInteractions(findReservaVacanteUseCase);
        verifyNoInteractions(updateReservaVacanteUseCase);
    }

    /**
     * Guarda de integridad simétrica a la de cuota: si el reservaVacanteId
     * del evento no coincide con el del contexto guardado, el use case debe
     * cortar temprano sin actualizar nada ni consultar la reserva.
     */
    @Test
    void reservaVacante_mismatchReservaVacanteId_noActualizaNiRegistraNada() {
        var context = MercadoPagoContext.builder()
                .mercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)
                .reservaVacanteId(RESERVA_VACANTE_ID)
                .build();
        var event = eventForReserva("approved").reservaVacanteId(UUID.randomUUID()).build(); // no coincide

        when(findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(MERCADO_PAGO_CONTEXT_ID)).thenReturn(context);

        useCase.processPaymentEvent(event);

        verifyNoInteractions(updateMercadoPagoContextUseCase);
        verifyNoInteractions(findReservaVacanteUseCase);
        verifyNoInteractions(updateReservaVacanteUseCase);
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