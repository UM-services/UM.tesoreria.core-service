package um.tesoreria.core.configuration;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import um.tesoreria.core.event.CorreoEvent;
import um.tesoreria.core.event.CorreoPayload;
import um.tesoreria.core.event.Remitente;
import um.tesoreria.core.event.SendChequeraEvent;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * El productor de {@code send-chequera} no debe escribir {@code __TypeId__}: el contrato es el
 * JSON del evento y el consumidor resuelve por tipo target.
 */
class KafkaProducerConfigTest {

    @Test
    void serializaSinTypeIdYConservaTodosLosCampos() {
        var config = new KafkaProducerConfig();
        var event = SendChequeraEvent.builder()
                .facultadId(1)
                .tipoChequeraId(2)
                .chequeraSerieId(3L)
                .alternativaId(1)
                .copiaInformes(false)
                .incluyeMatricula(true)
                .codigoBarras(true)
                .build();
        var headers = new RecordHeaders();

        byte[] payload = config.kafkaJsonSerializer().serialize("send-chequera", headers, event);

        assertThat(headers.lastHeader("__TypeId__")).isNull();
        var node = JsonMapper.builder().findAndAddModules().build()
                .readTree(new String(payload, StandardCharsets.UTF_8));
        assertThat(node.get("facultadId").asInt()).isEqualTo(1);
        assertThat(node.get("tipoChequeraId").asInt()).isEqualTo(2);
        assertThat(node.get("chequeraSerieId").asLong()).isEqualTo(3L);
        assertThat(node.get("alternativaId").asInt()).isEqualTo(1);
        assertThat(node.get("copiaInformes").asBoolean()).isFalse();
        assertThat(node.get("incluyeMatricula").asBoolean()).isTrue();
        assertThat(node.get("codigoBarras").asBoolean()).isTrue();
    }

    @Test
    void serializaElEnvelopeDeCorreoSinTypeId() {
        var config = new KafkaProducerConfig();
        var event = CorreoEvent.builder()
                .eventType(CorreoEvent.TIPO)
                .version(CorreoEvent.VERSION_ACTUAL)
                .eventId("evt-1")
                .occurredAt(OffsetDateTime.parse("2026-10-22T12:00:00Z"))
                .payload(CorreoPayload.builder()
                        .remitente(Remitente.NOTIFICACION)
                        .para(List.of("alumno@um.edu.ar"))
                        .replyTo("no-reply@um.edu.ar")
                        .asunto("Notificación por Deuda")
                        .cuerpo("Hola")
                        .build())
                .build();
        var headers = new RecordHeaders();

        byte[] payload = config.kafkaJsonSerializer().serialize("send-correo", headers, event);

        assertThat(headers.lastHeader("__TypeId__")).isNull();
        var node = JsonMapper.builder().findAndAddModules().build()
                .readTree(new String(payload, StandardCharsets.UTF_8));
        assertThat(node.get("eventType").asString()).isEqualTo("CORREO");
        assertThat(node.get("version").asInt()).isEqualTo(1);
        assertThat(node.get("eventId").asString()).isEqualTo("evt-1");
        assertThat(node.get("payload").get("remitente").asString()).isEqualTo("NOTIFICACION");
        assertThat(node.get("payload").get("para").get(0).asString()).isEqualTo("alumno@um.edu.ar");
        assertThat(node.get("payload").get("html").asBoolean()).isFalse();
    }

}
