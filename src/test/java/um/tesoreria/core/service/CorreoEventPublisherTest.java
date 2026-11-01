package um.tesoreria.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import um.tesoreria.core.event.CorreoEvent;
import um.tesoreria.core.event.CorreoPayload;
import um.tesoreria.core.event.Remitente;
import um.tesoreria.core.event.ReferenciaNotificacion;
import um.tesoreria.core.event.SendCuotaEvent;

class CorreoEventPublisherTest {

    @Test
    @SuppressWarnings("unchecked")
    void publicaElEnvelopeConIdentidadYVersion() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        var publisher = new CorreoEventPublisher(kafkaTemplate);
        var payload = CorreoPayload.builder()
                .remitente(Remitente.NOTIFICACION)
                .para(List.of("alumno@um.edu.ar"))
                .asunto("Notificación")
                .cuerpo("Hola")
                .referencia(ReferenciaNotificacion.builder().tipo("PERSONA_SUSPENDIDO").id("7").build())
                .build();

        String eventId = publisher.publicar(payload);

        ArgumentCaptor<CorreoEvent> captor = ArgumentCaptor.forClass(CorreoEvent.class);
        verify(kafkaTemplate).send(eq(CorreoEventPublisher.TOPIC_CORREO), eq(eventId), captor.capture());
        var event = captor.getValue();
        assertThat(event.getEventType()).isEqualTo(CorreoEvent.TIPO);
        assertThat(event.getVersion()).isEqualTo(CorreoEvent.VERSION_ACTUAL);
        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getOccurredAt()).isNotNull();
        assertThat(event.getPayload()).isSameAs(payload);
    }

    @Test
    @SuppressWarnings("unchecked")
    void publicaLaCuotaPorId() {
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        var publisher = new CorreoEventPublisher(kafkaTemplate);

        publisher.publicarCuota(9L);

        ArgumentCaptor<SendCuotaEvent> captor = ArgumentCaptor.forClass(SendCuotaEvent.class);
        verify(kafkaTemplate).send(eq(CorreoEventPublisher.TOPIC_CUOTA), captor.capture());
        assertThat(captor.getValue().getChequeraCuotaId()).isEqualTo(9L);
    }

}
