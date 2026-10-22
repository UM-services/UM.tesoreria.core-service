package um.tesoreria.core.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * El contrato del topic es el JSON del evento, no el FQCN de la clase Java del productor.
     * No se escribe __TypeId__: el consumidor usa el tipo target.
     */
    @Bean
    public JacksonJsonSerializer<Object> kafkaJsonSerializer() {
        JacksonJsonSerializer<Object> serializer = new JacksonJsonSerializer<>();
        serializer.setAddTypeInfo(false);
        return serializer;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(JacksonJsonSerializer<Object> kafkaJsonSerializer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), kafkaJsonSerializer);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(JacksonJsonSerializer<Object> kafkaJsonSerializer) {
        return new KafkaTemplate<>(producerFactory(kafkaJsonSerializer));
    }

    /**
     * Template exclusivo del DLT. Cuando el consumidor no puede deserializar el record, el
     * {@code DeadLetterPublishingRecoverer} republica los bytes originales: con el template JSON
     * esos bytes terminarían en base64 dentro de un string, así que se necesita un serializer
     * binario para conservar el payload tal cual llegó.
     */
    @Bean
    public ProducerFactory<String, byte[]> dltProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, byte[]> dltKafkaTemplate() {
        return new KafkaTemplate<>(dltProducerFactory());
    }

}
