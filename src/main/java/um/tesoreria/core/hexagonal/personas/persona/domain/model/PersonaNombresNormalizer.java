package um.tesoreria.core.hexagonal.personas.persona.domain.model;

/**
 * Normaliza los campos de nombre de una {@link Persona}:
 * <ul>
 *   <li>Apellido: completamente en mayúsculas (admite apellidos compuestos con espacios: "DE LA CRUZ").</li>
 *   <li>Nombre: primera letra en mayúsculas de cada nombre compuesto (separado por espacio o guión)
 *       y el resto en minúsculas: "Juan Pablo", "Ana Sofía", "María-Eugenia".</li>
 * </ul>
 * Ambas operaciones recortan espacios sobrantes, colapsan espacios múltiples a uno solo
 * y son Unicode-aware (respetan tildes y la ñ). Los valores {@code null} o vacíos se
 * devuelven sin alteración para no interferir con valores opcionales.
 */
public final class PersonaNombresNormalizer {

    private PersonaNombresNormalizer() {
    }

    public static String normalizarApellido(String apellido) {
        if (isBlank(apellido)) {
            return apellido;
        }
        return collapseSpaces(apellido).toUpperCase(java.util.Locale.ROOT);
    }

    public static String normalizarNombre(String nombre) {
        if (isBlank(nombre)) {
            return nombre;
        }
        String limpio = collapseSpaces(nombre);
        StringBuilder resultado = new StringBuilder(limpio.length());
        boolean inicioDePalabra = true;
        for (int i = 0; i < limpio.length(); ) {
            int codePoint = limpio.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            if (Character.isLetter(codePoint)) {
                if (inicioDePalabra) {
                    resultado.appendCodePoint(Character.toUpperCase(codePoint));
                    inicioDePalabra = false;
                } else {
                    resultado.appendCodePoint(Character.toLowerCase(codePoint));
                }
            } else {
                resultado.appendCodePoint(codePoint);
                // El guión también rompe la palabra: "maría-eugenia" -> "María-Eugenia"
                inicioDePalabra = true;
            }
            i += charCount;
        }
        return resultado.toString();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String collapseSpaces(String value) {
        return value.strip().replaceAll("\\s+", " ");
    }
}
