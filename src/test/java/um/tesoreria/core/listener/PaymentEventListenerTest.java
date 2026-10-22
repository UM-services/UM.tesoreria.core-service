package um.tesoreria.core.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private MercadoPagoContextService mercadoPagoContextService;

    @InjectMocks
    private PaymentEventListener listener;

    @Test
    void delegaElEventoAlServicioDeContextos() {
        var event = evento();

        listener.handlePaymentProcessed(event);

        verify(mercadoPagoContextService).processPaymentEvent(event);
        verifyNoMoreInteractions(mercadoPagoContextService);
    }

    /**
     * Si el listener se traga la excepción, el offset se commitea y el pago se pierde: el error
     * tiene que llegar al errorHandler para que Kafka reintente y derive al DLT.
     */
    @Test
    void propagaElErrorParaQueKafkaReintenteYDeriveAlDlt() {
        var error = new IllegalStateException("base de datos caida");
        doThrow(error).when(mercadoPagoContextService).processPaymentEvent(any());

        assertThatThrownBy(() -> listener.handlePaymentProcessed(evento()))
                .isSameAs(error);
    }

    private PaymentProcessedEvent evento() {
        return PaymentProcessedEvent.builder()
                .mercadoPagoContextId(5L)
                .chequeraCuotaId(7L)
                .paymentId("12345")
                .status("approved")
                .build();
    }

}
