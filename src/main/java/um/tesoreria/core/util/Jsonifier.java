package um.tesoreria.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class Jsonifier {

    private Jsonifier() {
        // Constructor privado para evitar instanciación de la clase de utilidad
    }

    /**
     * Crea un nuevo Builder para serializar el objeto proporcionado.
     *
     * @param object El objeto a serializar.
     * @param <T>    El tipo del objeto.
     * @return una instancia de Builder.
     */
    public static <T> Builder<T> builder(T object) {
        return new Builder<>(object);
    }

    public static class Builder<T> {
        private final T object;
        private boolean prettyPrint = true;

        private Builder(T object) {
            this.object = object;
        }

        /**
         * Configura si la salida JSON debe tener formato "pretty-print".
         *
         * @param pretty true para habilitar pretty-print (por defecto), false para deshabilitarlo.
         * @return la misma instancia de Builder.
         */
        public Builder<T> pretty(boolean pretty) {
            this.prettyPrint = pretty;
            return this;
        }

        /**
         * Realiza la serialización JSON.
         *
         * @return una cadena con el objeto en formato JSON.
         */
        public String build() {
            return jsonify();
        }

        /**
         * Realiza la serialización JSON.
         *
         * @return una cadena con el objeto en formato JSON.
         */
        private String jsonify() {
            try {
                ObjectMapper mapper = JsonMapper.builder()
                        .findAndAddModules()
                        .build();

                ObjectWriter writer = prettyPrint
                        ? mapper.writerWithDefaultPrettyPrinter()
                        : mapper.writer();

                return writer.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                return "jsonify error: " + e.getMessage();
            }
        }
    }
}
