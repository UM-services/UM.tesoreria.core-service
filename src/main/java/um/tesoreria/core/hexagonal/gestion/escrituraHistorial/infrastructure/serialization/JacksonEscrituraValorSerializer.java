package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraValorSerializer;

/**
 * Serializa estados a JSON compacto. Null → cadena vacía (alta sin anterior / baja sin posterior).
 */
@Component
public class JacksonEscrituraValorSerializer implements EscrituraValorSerializer {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public String serialize(Object valor) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof String s) {
            return s;
        }
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("No se pudo serializar el valor de historial: " + e.getOriginalMessage(), e);
        }
    }
}
