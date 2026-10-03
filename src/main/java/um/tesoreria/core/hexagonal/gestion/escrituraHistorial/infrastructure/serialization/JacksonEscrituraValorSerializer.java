package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Entity;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraValorSerializer;

/**
 * Serializa estados a JSON compacto. Null → NULL (alta sin anterior / baja sin posterior).
 * Fechas en ISO-8601, decimales sin notación científica y claves ordenadas: el historial no se
 * reescribe, así que el formato queda fijo para siempre. Las claves de un {@code Map} solo pueden
 * ser texto, enteros, enums o booleanos: al ordenarlas, dos claves que comparan igual
 * ({@code BigDecimal} 1.0 y 1.00) se pisarían sin aviso.
 */
@Component
public class JacksonEscrituraValorSerializer implements EscrituraValorSerializer {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            // Módulos explícitos: una dependencia nueva no puede cambiar el JSON de filas que no se reescriben
            .addModule(new JavaTimeModule())
            .addModule(new SimpleModule().setSerializerModifier(new RechazaPersistentes()))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            // Dos claves distintas que salen con el mismo nombre en el JSON perderían una entrada al leerlo
            .enable(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();

    @Override
    public String serialize(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (JsonProcessingException e) {
            for (Throwable t = e; t != null; t = t.getCause()) {
                if (t instanceof ValorRechazado rechazo) {
                    throw new IllegalArgumentException(rechazo.getMessage());
                }
                if (t instanceof JsonGenerationException g && g.getOriginalMessage() != null
                        && g.getOriginalMessage().startsWith("Duplicate field")) {
                    // Sin el nombre de la clave: puede ser un valor del estado
                    throw new IllegalArgumentException("El valor de historial tiene claves que se repiten en el JSON ("
                            + valor.getClass().getSimpleName() + ")");
                }
            }
            // Sin el mensaje ni la causa original: pueden traer valores del estado
            throw new IllegalArgumentException("No se pudo serializar el valor de historial ("
                    + valor.getClass().getSimpleName() + ": " + e.getClass().getSimpleName() + ")");
        }
    }

    /**
     * Una entidad administrada, un proxy lazy o una colección de Hibernate se pisan con el save del
     * caller o cargan relaciones: el historial guardaría el estado nuevo como anterior. Se rechazan
     * en cualquier nivel del valor, no solo en la raíz.
     */
    private static final class RechazaPersistentes extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifySerializer(
                SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
            return esPersistente(desc.getBeanClass()) ? new Rechazo(desc.getBeanClass()) : serializer;
        }

        @Override
        public JsonSerializer<?> modifyCollectionSerializer(
                SerializationConfig config, CollectionType type, BeanDescription desc, JsonSerializer<?> serializer) {
            return esPersistente(desc.getBeanClass()) ? new Rechazo(desc.getBeanClass()) : serializer;
        }

        @Override
        public JsonSerializer<?> modifyCollectionLikeSerializer(
                SerializationConfig config, CollectionLikeType type, BeanDescription desc, JsonSerializer<?> serializer) {
            return esPersistente(desc.getBeanClass()) ? new Rechazo(desc.getBeanClass()) : serializer;
        }

        // PersistentMap y PersistentSortedMap pasan por acá, no por modifyCollectionSerializer
        @Override
        public JsonSerializer<?> modifyMapSerializer(
                SerializationConfig config, MapType type, BeanDescription desc, JsonSerializer<?> serializer) {
            return esPersistente(desc.getBeanClass()) ? new Rechazo(desc.getBeanClass()) : serializer;
        }

        @Override
        public JsonSerializer<?> modifyMapLikeSerializer(
                SerializationConfig config, MapLikeType type, BeanDescription desc, JsonSerializer<?> serializer) {
            return esPersistente(desc.getBeanClass()) ? new Rechazo(desc.getBeanClass()) : serializer;
        }

        // Las claves no pasan por los demás métodos: sin esto una entidad como clave se guardaría con su toString()
        @Override
        public JsonSerializer<?> modifyKeySerializer(
                SerializationConfig config, JavaType keyType, BeanDescription desc, JsonSerializer<?> serializer) {
            return new ClaveControlada(serializer);
        }

        private static boolean esPersistente(Class<?> tipo) {
            return tipo.isAnnotationPresent(Entity.class)
                    || HibernateProxy.class.isAssignableFrom(tipo)
                    || PersistentCollection.class.isAssignableFrom(tipo);
        }
    }

    private static final class ClaveControlada extends StdSerializer<Object> {

        private final JsonSerializer<Object> delegado;

        @SuppressWarnings("unchecked")
        private ClaveControlada(JsonSerializer<?> delegado) {
            super(Object.class);
            this.delegado = (JsonSerializer<Object>) delegado;
        }

        @Override
        public void serialize(Object clave, JsonGenerator gen, SerializerProvider provider) throws java.io.IOException {
            if (!clavePermitida(clave)) {
                throw new ValorRechazado("Clave de Map no permitida en el historial ("
                        + clave.getClass().getSimpleName() + "): usar texto, enteros, enums o booleanos");
            }
            delegado.serialize(clave, gen, provider);
        }

        private static boolean clavePermitida(Object clave) {
            return clave instanceof String || clave instanceof Character || clave instanceof Boolean
                    || clave instanceof Enum<?>
                    || clave instanceof Integer || clave instanceof Long || clave instanceof Short
                    || clave instanceof Byte || clave instanceof java.math.BigInteger;
        }
    }

    private static final class Rechazo extends StdSerializer<Object> {

        private final Class<?> tipo;

        private Rechazo(Class<?> tipo) {
            super(Object.class);
            this.tipo = tipo;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) {
            throw new ValorRechazado("El historial recibe una copia del estado, no la entidad JPA " + tipo.getSimpleName());
        }
    }

    /** Rechazo propio, con un mensaje que no incluye valores del estado. */
    private static final class ValorRechazado extends RuntimeException {

        private ValorRechazado(String mensaje) {
            super(mensaje);
        }
    }
}
