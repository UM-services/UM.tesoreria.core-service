package um.tesoreria.core.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

/**
 * Topics de los que {@code tesoreria-core} es dueño.
 *
 * <p>El dueño de un topic es el servicio que lo declara: el productor para el topic principal y
 * el consumidor que deriva para su DLT. Los demás servicios asumen que existe y, si falta, fallan
 * ruidosamente en lugar de crearlo implícitamente por {@code auto.create.topics.enable}.</p>
 *
 * <p>Spring Boot crea estos {@code NewTopic} al arrancar mediante el {@code KafkaAdmin}
 * auto-configurado (bootstrap desde {@code spring.kafka.bootstrap-servers}). El réplication factor
 * es ambiental ({@code app.kafka.topics.replicas}: 1 con el broker único actual, 3 en la fase de
 * cluster); las particiones se mantienen en 1 porque así están los topics existentes y el volumen
 * no requiere más. {@code KafkaAdmin} nunca cambia el réplication factor de un topic existente.</p>
 */
@Configuration
public class KafkaTopicsConfiguration {

    private static final String DLT_RETENTION_MS = "1209600000"; // 14 días

    @Bean
    NewTopic sendChequeraTopic(@Value("${app.kafka.topics.partitions:1}") int partitions,
                               @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return newTopic("send-chequera", partitions, replicas, null);
    }

    @Bean
    NewTopic sendCuotaTopic(@Value("${app.kafka.topics.partitions:1}") int partitions,
                            @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return newTopic("send-cuota", partitions, replicas, null);
    }

    @Bean
    NewTopic sendCorreoTopic(@Value("${app.kafka.topics.partitions:1}") int partitions,
                             @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return newTopic("send-correo", partitions, replicas, null);
    }

    /**
     * DLT de {@code payment-processed}: la escribe este servicio (y {@code umhub}), porque es el
     * consumidor que deriva. Mismas particiones que el topic origen.
     */
    @Bean
    NewTopic paymentProcessedDltTopic(@Value("${app.kafka.topics.partitions:1}") int partitions,
                                      @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return newTopic("payment-processed.DLT", partitions, replicas, DLT_RETENTION_MS);
    }

    /**
     * DLT de {@code notificacion-resultado}: lo deriva el listener de este servicio cuando no
     * puede confirmar la notificación. Antes no estaba declarado y sólo existía si el broker
     * tenía {@code auto.create.topics.enable=true}, con retención default.
     */
    @Bean
    NewTopic notificacionResultadoDltTopic(@Value("${app.kafka.topics.partitions:1}") int partitions,
                                           @Value("${app.kafka.topics.replicas:1}") int replicas) {
        return newTopic("notificacion-resultado.DLT", partitions, replicas, DLT_RETENTION_MS);
    }

    private NewTopic newTopic(String name, int partitions, int replicas, String retentionMs) {
        var builder = TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .config("min.insync.replicas", String.valueOf(Math.min(2, replicas)));
        if (retentionMs != null) {
            builder.configs(Map.of("retention.ms", retentionMs, "cleanup.policy", "delete"));
        }
        return builder.build();
    }
}
