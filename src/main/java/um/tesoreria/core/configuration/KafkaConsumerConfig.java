package um.tesoreria.core.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;
import um.tesoreria.core.event.NotificacionResultadoEvent;
import um.tesoreria.core.event.PaymentProcessedEvent;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

    /**
     * Reintentos antes de derivar a {@code payment-processed.DLT}. Con 1 s inicial y factor 2 el
     * último reintento cae a los ~16 s: suficiente para una caída corta de la base sin bloquear la
     * partición durante minutos.
     */
    static final long REINTENTOS_PAGO_PROCESADO = 5L;
    static final long ESPERA_INICIAL_PAGO_PROCESADO = 1000L;
    static final double FACTOR_PAGO_PROCESADO = 2.0;

    static final String SUFIJO_DLT = ".DLT";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, PaymentProcessedEvent> consumerFactory() {
        log.info("Initializing Kafka Consumer Factory with bootstrapServers: {} and groupId: {}", bootstrapServers, groupId);
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        JsonMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        /*
         * El contrato del topic es el JSON del evento, no el nombre de clase Java del productor.
         * useHeadersIfPresent=false evita resolver tipos desde __TypeId__ (y elimina el riesgo
         * de deserializacion arbitraria); el tipo target es la unica fuente de verdad.
         */
        var jsonDeserializer = new JacksonJsonDeserializer<PaymentProcessedEvent>(
                PaymentProcessedEvent.class, objectMapper, false);
        var errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> kafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTemplate<String, byte[]> dltKafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler(kafkaTemplate, dltKafkaTemplate));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, NotificacionResultadoEvent> notificacionResultadoConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        JsonMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        var jsonDeserializer = new JacksonJsonDeserializer<NotificacionResultadoEvent>(
                NotificacionResultadoEvent.class, objectMapper, false);
        var errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificacionResultadoEvent> notificacionResultadoKafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTemplate<String, byte[]> dltKafkaTemplate) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, NotificacionResultadoEvent>();
        factory.setConsumerFactory(notificacionResultadoConsumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler(kafkaTemplate, dltKafkaTemplate));
        return factory;
    }

    /**
     * Reintentos con backoff exponencial y derivación a {@code payment-processed.DLT} cuando se
     * agotan. Se elige el template según el valor a publicar: un evento ya deserializado viaja como
     * JSON y un record que no se pudo deserializar viaja como bytes crudos.
     */
    DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, Object> kafkaTemplate,
                                            KafkaTemplate<String, byte[]> dltKafkaTemplate) {
        Map<Class<?>, KafkaOperations<?, ?>> templates = new HashMap<>();
        templates.put(PaymentProcessedEvent.class, kafkaTemplate);
        templates.put(NotificacionResultadoEvent.class, kafkaTemplate);
        templates.put(byte[].class, dltKafkaTemplate);

        var recoverer = new DeadLetterPublishingRecoverer(templates, KafkaConsumerConfig::destinoDlt);
        /*
         * Un JSON inválido (DeserializationException) ya viene clasificado como fatal por Spring
         * Kafka: no consume reintentos y va directo al DLT. El resto de las excepciones agota los
         * reintentos antes de derivar.
         */
        return new DefaultErrorHandler(recoverer, backOffPagoProcesado());
    }

    static ExponentialBackOff backOffPagoProcesado() {
        var backOff = new ExponentialBackOff(ESPERA_INICIAL_PAGO_PROCESADO, FACTOR_PAGO_PROCESADO);
        backOff.setMaxAttempts(REINTENTOS_PAGO_PROCESADO);
        return backOff;
    }

    static TopicPartition destinoDlt(ConsumerRecord<?, ?> record, Exception exception) {
        return new TopicPartition(record.topic() + SUFIJO_DLT, record.partition());
    }

}
