package um.tesoreria.core.configuration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import um.tesoreria.core.event.PaymentProcessedEvent;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Verifica que el consumidor deserialice por tipo target y no dependa de __TypeId__, y que los
 * errores de procesamiento tengan reintentos con backoff y derivación al topic DLT.
 */
class KafkaConsumerConfigTest {

    private static final String TOPIC = "payment-processed";

    private DefaultKafkaConsumerFactory<String, PaymentProcessedEvent> factory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        var config = new KafkaConsumerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "kafka:9092");
        ReflectionTestUtils.setField(config, "groupId", "tesoreria-core-group");
        factory = (DefaultKafkaConsumerFactory<String, PaymentProcessedEvent>) config.consumerFactory();
    }

    @Test
    void deserializaElEventoSinHeadersDeTipo() {
        PaymentProcessedEvent event = deserialize(new RecordHeaders(), payload());

        assertThat(event.getMercadoPagoContextId()).isEqualTo(5L);
        assertThat(event.getChequeraCuotaId()).isEqualTo(7L);
        assertThat(event.getReservaVacanteId()).isEqualTo(java.util.UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(event.getPaymentId()).isEqualTo("12345");
        assertThat(event.getStatus()).isEqualTo("approved");
        assertThat(event.getStatusDetail()).isEqualTo("accredited");
        assertThat(event.getDateApproved()).isEqualTo(OffsetDateTime.of(2026, 10, 20, 12, 0, 0, 0, ZoneOffset.UTC));
        assertThat(event.getDateCreated()).isEqualTo(OffsetDateTime.of(2026, 10, 20, 11, 0, 0, 0, ZoneOffset.UTC));
        assertThat(event.getDateLastUpdated()).isEqualTo(OffsetDateTime.of(2026, 10, 20, 12, 30, 0, 0, ZoneOffset.UTC));
        assertThat(event.getSource()).isEqualTo("WEBHOOK_V1");
        assertThat(event.getTransactionAmount()).isEqualByComparingTo("1234.50");
        assertThat(event.getPaymentJson()).isEqualTo("{\"id\":12345}");
    }

    @Test
    void ignoraElTypeIdDelProductorActual() {
        Headers headers = new RecordHeaders();
        headers.add("__TypeId__", "um.tesoreria.mercadopago.domain.event.PaymentProcessedEvent"
                .getBytes(StandardCharsets.UTF_8));

        assertThat(deserialize(headers, payload()).getPaymentId()).isEqualTo("12345");
    }

    @Test
    void ignoraUnTypeIdDeUnPaqueteInexistente() {
        Headers headers = new RecordHeaders();
        headers.add("__TypeId__", "com.desconocido.Evento".getBytes(StandardCharsets.UTF_8));

        assertThat(deserialize(headers, payload()).getPaymentId()).isEqualTo("12345");
    }

    @Test
    void toleraCamposNuevosParaEvolucionarElContrato() {
        String payload = """
                {"mercadoPagoContextId":5,"chequeraCuotaId":7,"paymentId":"12345",
                 "status":"approved","statusDetail":"accredited",
                 "dateApproved":"2026-10-20T12:00:00Z","dateCreated":"2026-10-20T11:00:00Z",
                 "transactionAmount":1234.50,"paymentJson":"{\\"id\\":12345}",
                 "schemaVersion":1,"campoFuturo":"ignorado"}
                """;

        PaymentProcessedEvent event = deserialize(new RecordHeaders(), payload);

        assertThat(event.getPaymentId()).isEqualTo("12345");
        assertThat(event.getTransactionAmount()).isEqualByComparingTo("1234.50");
    }

    @Test
    void usaBackoffExponencialConCincoReintentos() {
        var backOff = KafkaConsumerConfig.backOffPagoProcesado();

        assertThat(backOff.getInitialInterval()).isEqualTo(1000L);
        assertThat(backOff.getMultiplier()).isEqualTo(2.0);
        assertThat(backOff.getMaxAttempts()).isEqualTo(5L);
    }

    @Test
    void losErroresSeDerivanAlTopicDltDeLaMismaParticion() {
        var record = new ConsumerRecord<>(TOPIC, 2, 42L, "12345", (PaymentProcessedEvent) null);

        var destino = KafkaConsumerConfig.destinoDlt(record, new IllegalStateException("base caida"));

        assertThat(destino.topic()).isEqualTo("payment-processed.DLT");
        assertThat(destino.partition()).isEqualTo(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void elContenedorSeArmaConElManejadorDeErroresYLosDosTemplatesDelDlt() {
        var config = new KafkaConsumerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "kafka:9092");
        ReflectionTestUtils.setField(config, "groupId", "tesoreria-core-group");

        var listenerFactory = config.kafkaListenerContainerFactory(
                mock(KafkaTemplate.class), mock(KafkaTemplate.class));

        assertThat(listenerFactory.getContainerProperties()).isNotNull();
        assertThat(listenerFactory.getContainerProperties().getTopics()).isNull();
    }

    private PaymentProcessedEvent deserialize(Headers headers, String payload) {
        return factory.getValueDeserializer()
                .deserialize(TOPIC, headers, payload.getBytes(StandardCharsets.UTF_8));
    }

    private String payload() {
        return """
                {"mercadoPagoContextId":5,"chequeraCuotaId":7,
                 "reservaVacanteId":"550e8400-e29b-41d4-a716-446655440000",
                 "paymentId":"12345","status":"approved","statusDetail":"accredited",
                 "dateApproved":"2026-10-20T12:00:00Z","dateCreated":"2026-10-20T11:00:00Z",
                 "dateLastUpdated":"2026-10-20T12:30:00Z","source":"WEBHOOK_V1",
                 "transactionAmount":1234.50,"paymentJson":"{\\"id\\":12345}"}
                """;
    }

}
