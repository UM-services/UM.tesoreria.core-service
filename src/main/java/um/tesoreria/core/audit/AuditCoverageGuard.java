package um.tesoreria.core.audit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Guard estático contra escrituras que saltean el ciclo de vida de JPA y, con él, la
 * auditoría de operaciones.
 *
 * <p>La regla: toda escritura de negocio debe pasar por una entidad JPA ({@code save},
 * {@code delete}, {@code deleteById} o un derived delete) para que Hibernate dispare
 * {@code PostInsert}/{@code PostUpdate}/{@code PostDelete}. Si una escritura no puede pasar
 * por JPA —correlativos atómicos con {@code LAST_INSERT_ID}, por ejemplo— debe emitir su
 * evento con {@link AuditManualChange} en la misma transacción y figurar en
 * {@code src/test/resources/audit-coverage-exceptions.txt} con su motivo.</p>
 *
 * <p><b>Qué no marca</b> (y antes marcaba, generando 37 falsos positivos):</p>
 * <ul>
 *   <li>{@code @Modifying} en un delete derivado: Spring Data lo ignora, el delete corre con
 *       {@code em.remove} y sí dispara los listeners.</li>
 *   <li>{@code @Query} de lectura: no es una escritura.</li>
 *   <li>{@code JdbcTemplate} usado sólo para consultar.</li>
 * </ul>
 *
 * <p>El paquete {@code um.tesoreria.core.audit} queda excluido: el outbox escribe con JDBC a
 * propósito (es el canal de auditoría, no datos de negocio).</p>
 */
public final class AuditCoverageGuard {

    /** Patrones de DML directo que siempre son una violación. */
    private static final List<String> ALWAYS_FORBIDDEN = List.of(
            "createNativeQuery",
            "createMutationQuery",
            "StatelessSession",
            "deleteAllInBatch",
            "deleteAllByIdInBatch",
            "@SQLDelete",
            "@SQLInsert",
            "@SQLUpdate",
            "executeUpdate",
            "@Procedure",
            "SimpleJdbcInsert",
            "doWork",
            "getConnection()");

    /** APIs de acceso directo a JDBC: sólo son violación si el archivo además escribe. */
    private static final List<String> JDBC_APIS = List.of(
            "JdbcTemplate",
            "NamedParameterJdbcTemplate",
            "JdbcClient");

    private static final List<String> JDBC_WRITE_CALLS = List.of(
            ".update(",
            ".batchUpdate(",
            ".execute(",
            "batchUpdate(");

    private static final Pattern QUERY_ANNOTATION = Pattern.compile("@Query\\s*\\(");

    private static final String AUDIT_PACKAGE = "/um/tesoreria/core/audit/";

    private AuditCoverageGuard() {
    }

    /**
     * Recorre todos los {@code *.java} y {@code *.kt} bajo {@code root}, salvo el paquete de
     * auditoría, e informa cada escritura que no pasa por JPA (los comentarios se ignoran).
     *
     * @param root raíz del código de producción (por ejemplo {@code src/main/java})
     * @return violaciones ordenadas por archivo y línea; vacío si el código está limpio
     */
    public static List<Violation> scan(Path root) {
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("No existe el directorio a escanear: " + root);
        }

        try (Stream<Path> files = Files.walk(root)) {
            List<Violation> violations = new ArrayList<>();
            files.filter(Files::isRegularFile)
                    .filter(AuditCoverageGuard::isSourceFile)
                    .filter(path -> !isAuditPackage(path))
                    .sorted()
                    .forEach(path -> violations.addAll(scanFile(root, path)));
            violations.sort((a, b) -> {
                int byFile = a.file().compareTo(b.file());
                return byFile != 0 ? byFile : Integer.compare(a.line(), b.line());
            });
            return List.copyOf(violations);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escanear " + root, e);
        }
    }

    private static boolean isSourceFile(Path path) {
        String name = path.getFileName().toString();
        return name.endsWith(".java") || name.endsWith(".kt");
    }

    private static boolean isAuditPackage(Path path) {
        return ("/" + path.toString().replace('\\', '/')).contains(AUDIT_PACKAGE);
    }

    private static List<Violation> scanFile(Path root, Path file) {
        String source;
        try {
            source = Files.readString(file);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + file, e);
        }

        String code = String.join("\n", codeLines(source));
        Path display = root.relativize(file);
        List<Violation> violations = new ArrayList<>();

        for (String pattern : ALWAYS_FORBIDDEN) {
            int index = code.indexOf(pattern);
            if (index >= 0) {
                violations.add(new Violation(display, lineOf(code, index), pattern));
            }
        }

        if (writesWithJdbc(code)) {
            violations.add(new Violation(display, jdbcLine(code), "JdbcTemplate"));
        }

        int queryIndex = code.indexOf("@Query");
        while (queryIndex >= 0) {
            int closing = closingParenthesis(code, queryIndex + "@Query".length());
            if (closing > 0) {
                String annotation = code.substring(queryIndex, closing + 1);
                if (isDmlQuery(annotation)) {
                    violations.add(new Violation(display, lineOf(code, queryIndex), "@Query(DML)"));
                }
            }
            queryIndex = code.indexOf("@Query", queryIndex + 1);
        }

        return violations;
    }

    private static boolean writesWithJdbc(String code) {
        boolean usesJdbc = JDBC_APIS.stream().anyMatch(code::contains);
        if (!usesJdbc) {
            return false;
        }
        return JDBC_WRITE_CALLS.stream().anyMatch(code::contains);
    }

    private static int jdbcLine(String code) {
        int index = code.indexOf(".update(");
        if (index < 0) {
            index = code.indexOf(".batchUpdate(");
        }
        if (index < 0) {
            index = code.indexOf(".execute(");
        }
        return index < 0 ? 1 : lineOf(code, index);
    }

    /**
     * Una {@code @Query} es escritura si el primer literal arranca con {@code update},
     * {@code delete} o {@code insert}. Las de lectura no se marcan.
     */
    static boolean isDmlQuery(String annotation) {
        String literal = firstStringLiteral(annotation);
        if (literal == null) {
            return false;
        }
        String normalized = literal.strip().toLowerCase(Locale.ROOT);
        return normalized.startsWith("update")
                || normalized.startsWith("delete")
                || normalized.startsWith("insert");
    }

    static String firstStringLiteral(String text) {
        int start = text.indexOf('"');
        if (start < 0) {
            return null;
        }
        boolean textBlock = text.startsWith("\"\"\"", start);
        int contentStart = start + (textBlock ? 3 : 1);
        int index = contentStart;
        while (index < text.length()) {
            if (textBlock && text.startsWith("\"\"\"", index)) {
                return text.substring(contentStart, index);
            }
            char current = text.charAt(index);
            if (!textBlock && current == '\\' && index + 1 < text.length()) {
                index += 2;
                continue;
            }
            if (!textBlock && current == '"') {
                return text.substring(contentStart, index);
            }
            index++;
        }
        return text.substring(contentStart);
    }

    private static int closingParenthesis(String code, int from) {
        int open = code.indexOf('(', from);
        if (open < 0) {
            return -1;
        }
        int depth = 0;
        boolean inString = false;
        boolean inTextBlock = false;
        for (int index = open; index < code.length(); index++) {
            if (!inString && !inTextBlock && code.startsWith("\"\"\"", index)) {
                inTextBlock = true;
                index += 2;
                continue;
            }
            if (inTextBlock) {
                if (code.startsWith("\"\"\"", index)) {
                    inTextBlock = false;
                    index += 2;
                }
                continue;
            }
            char current = code.charAt(index);
            if (current == '\\') {
                index++;
                continue;
            }
            if (current == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth--;
                if (depth == 0) {
                    return index;
                }
            }
        }
        return -1;
    }

    private static int lineOf(String code, int index) {
        int line = 1;
        for (int position = 0; position < index && position < code.length(); position++) {
            if (code.charAt(position) == '\n') {
                line++;
            }
        }
        return line;
    }

    /**
     * Devuelve el código del archivo con los comentarios reemplazados por espacios,
     * conservando los saltos de línea para no perder la numeración. Los literales de texto
     * (incluidos los text blocks) se dejan intactos.
     */
    static List<String> codeLines(String source) {
        StringBuilder code = new StringBuilder(source.length());
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            char next = index + 1 < source.length() ? source.charAt(index + 1) : '\0';

            if (current == '/' && next == '/') {
                while (index < source.length() && source.charAt(index) != '\n') {
                    code.append(' ');
                    index++;
                }
            } else if (current == '/' && next == '*') {
                code.append("  ");
                index += 2;
                while (index < source.length()) {
                    if (source.charAt(index) == '*' && index + 1 < source.length()
                            && source.charAt(index + 1) == '/') {
                        code.append("  ");
                        index += 2;
                        break;
                    }
                    code.append(source.charAt(index) == '\n' ? '\n' : ' ');
                    index++;
                }
            } else if (source.startsWith("\"\"\"", index)) {
                code.append("\"\"\"");
                index += 3;
                while (index < source.length()) {
                    if (source.startsWith("\"\"\"", index)) {
                        code.append("\"\"\"");
                        index += 3;
                        break;
                    }
                    code.append(source.charAt(index));
                    index++;
                }
            } else if (current == '"' || current == '\'') {
                char quote = current;
                code.append(quote);
                index++;
                while (index < source.length()) {
                    char inside = source.charAt(index);
                    if (inside == '\\' && index + 1 < source.length()) {
                        code.append(inside).append(source.charAt(index + 1));
                        index += 2;
                        continue;
                    }
                    code.append(inside);
                    index++;
                    if (inside == quote) {
                        break;
                    }
                }
            } else {
                code.append(current);
                index++;
            }
        }

        List<String> lines = new ArrayList<>();
        int start = 0;
        for (int position = 0; position < code.length(); position++) {
            if (code.charAt(position) == '\n') {
                lines.add(code.substring(start, position));
                start = position + 1;
            }
        }
        lines.add(code.substring(start));
        return lines;
    }

    /** Identificador estable de una violación: {@code archivo|patrón}. */
    public static String key(Violation violation) {
        return violation.file().toString().replace('\\', '/') + "|" + violation.pattern();
    }

    /** Una aparición de una escritura fuera de JPA: archivo, línea y patrón. */
    public record Violation(Path file, int line, String pattern) {

        public String message() {
            return "Escritura fuera de la auditoría en %s:%d: '%s'. "
                    .formatted(file.toString().replace('\\', '/'), line, pattern)
                    + "Pasala a JPA o emití el evento con AuditManualChange y declaralo en "
                    + "src/test/resources/audit-coverage-exceptions.txt "
                    + "(ver PLANIFICACION_AUDITORIA_TESORERIA.md).";
        }

        @Override
        public String toString() {
            return message();
        }
    }
}
