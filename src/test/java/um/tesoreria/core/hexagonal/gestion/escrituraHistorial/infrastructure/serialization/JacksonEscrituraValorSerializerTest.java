package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonEscrituraValorSerializerTest {

    private final JacksonEscrituraValorSerializer serializer = new JacksonEscrituraValorSerializer();

    @Test
    void nullQuedaVacio() {
        assertThat(serializer.serialize(null)).isEmpty();
    }

    @Test
    void stringSeConserva() {
        assertThat(serializer.serialize("ya-serializado")).isEqualTo("ya-serializado");
    }

    @Test
    void objetoAJsonCompacto() {
        Map<String, Object> valor = new LinkedHashMap<>();
        valor.put("id", 1);
        valor.put("nombre", "X");
        assertThat(serializer.serialize(valor)).isEqualTo("{\"id\":1,\"nombre\":\"X\"}");
    }
}
