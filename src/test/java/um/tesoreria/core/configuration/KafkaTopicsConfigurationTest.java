package um.tesoreria.core.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTopicsConfigurationTest {

    private final KafkaTopicsConfiguration configuration = new KafkaTopicsConfiguration();

    @Test
    void declaraLosTopicsDeCorreoConUnaParticion() {
        assertTopic(configuration.sendChequeraTopic(1, 1), "send-chequera", 1, 1);
        assertTopic(configuration.sendCuotaTopic(1, 1), "send-cuota", 1, 1);
        assertTopic(configuration.sendCorreoTopic(1, 1), "send-correo", 1, 1);
    }

    @Test
    void declaraLaDltDePaymentProcessedConRetencionExtendida() {
        NewTopic topic = configuration.paymentProcessedDltTopic(1, 1);

        assertTopic(topic, "payment-processed.DLT", 1, 1);
        assertThat(topic.configs()).containsEntry("retention.ms", "1209600000");
    }

    @Test
    void ajustaMinInsyncReplicasAlReplicationFactor() {
        assertThat(configuration.sendChequeraTopic(1, 1).configs())
                .containsEntry("min.insync.replicas", "1");
        assertThat(configuration.sendChequeraTopic(3, 3).configs())
                .containsEntry("min.insync.replicas", "2");
        // configs() de TopicBuilder fusiona: la DLT conserva min.isr y su retención
        assertThat(configuration.paymentProcessedDltTopic(3, 3).configs())
                .containsEntry("min.insync.replicas", "2")
                .containsEntry("retention.ms", "1209600000");
    }

    private void assertTopic(NewTopic topic, String name, int partitions, int replicas) {
        assertThat(topic.name()).isEqualTo(name);
        assertThat(topic.numPartitions()).isEqualTo(partitions);
        assertThat(topic.replicationFactor()).isEqualTo((short) replicas);
    }
}
