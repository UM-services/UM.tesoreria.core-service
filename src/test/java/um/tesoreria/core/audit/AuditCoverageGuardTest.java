package um.tesoreria.core.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Impide que aparezcan escrituras que no pasen por la auditoría.
 *
 * <p>La lista de excepciones ({@code src/test/resources/audit-coverage-exceptions.txt}) es una
 * allowlist explícita: cada entrada lleva motivo y, si es JDBC, el archivo tiene que emitir el
 * evento con {@code AuditManualChange}. Una excepción que ya no corresponde también rompe el
 * test, para que la lista no se podra.</p>
 *
 * <p>Para regenerar la lista después de una migración deliberada:</p>
 * <pre>{@code mvn test -Dtest=AuditCoverageGuardTest -Daudit.exceptions.write=true}</pre>
 */
class AuditCoverageGuardTest {

    private static final Path SOURCE_ROOT = Path.of(System.getProperty("user.dir"), "src/main/java");
    private static final Path EXCEPTIONS =
            Path.of(System.getProperty("user.dir"), "src/test/resources/audit-coverage-exceptions.txt");

    @Test
    void todaEscrituraEstaCubierta() throws IOException {
        Set<String> current = keys(AuditCoverageGuard.scan(SOURCE_ROOT));

        if (Boolean.getBoolean("audit.exceptions.write")) {
            writeExceptions(current);
            return;
        }

        Map<String, String> exceptions = readExceptions();

        List<String> sinDeclarar = current.stream()
                .filter(key -> !exceptions.containsKey(key))
                .sorted()
                .toList();
        assertThat(sinDeclarar)
                .withFailMessage(() -> """
                        Hay escrituras nuevas fuera de la auditoría:
                        %s

                        Pasalas a JPA o emiti el evento con AuditManualChange y declaralas en
                        src/test/resources/audit-coverage-exceptions.txt con su motivo.""".formatted(
                        String.join("\n", sinDeclarar)))
                .isEmpty();

        List<String> obsoletas = exceptions.keySet().stream()
                .filter(key -> !current.contains(key))
                .sorted()
                .toList();
        assertThat(obsoletas)
                .withFailMessage(() -> """
                        Excepciones de auditoría que ya no aplican (el archivo se migró o borró):
                        %s

                        Quitalas de audit-coverage-exceptions.txt.""".formatted(
                        String.join("\n", obsoletas)))
                .isEmpty();

        exceptions.forEach(this::verificaEmisionExplicita);
    }

    private void verificaEmisionExplicita(String key, String motivo) {
        String pattern = key.substring(key.indexOf('|') + 1);
        if (!pattern.startsWith("Jdbc") && !pattern.startsWith("NamedParameter")) {
            return;
        }
        String file = key.substring(0, key.indexOf('|'));
        String source;
        try {
            source = Files.readString(SOURCE_ROOT.resolve(file));
        } catch (IOException e) {
            throw new IllegalStateException("La excepción apunta a un archivo inexistente: " + file, e);
        }
        assertThat(source)
                .withFailMessage(() -> """
                        '%s' está exceptuado como '%s' (%s) pero no emite el evento.
                        Toda escritura JDBC tiene que llamar a AuditManualChange.""".formatted(
                        file, pattern, motivo))
                .contains("auditManual.");
    }

    @Test
    void unDeleteDerivadoConModifyingNoEsUnaViolacion(@TempDir Path tempDir) throws IOException {
        Path source = tempDir.resolve("um/tesoreria/core/repository/Foo.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package um.tesoreria.core.repository;

                public interface Foo {

                    // @Modifying en un delete derivado: Spring Data lo ignora y el delete
                    // corre con em.remove, así que dispara PostDelete.
                    @Modifying
                    void deleteAllByAnhoAndMes(Integer anho, Integer mes);
                }
                """);

        assertThat(AuditCoverageGuard.scan(tempDir)).isEmpty();
    }

    @Test
    void unDeleteMasivoConQueryEsUnaViolacion(@TempDir Path tempDir) throws IOException {
        Path source = tempDir.resolve("um/tesoreria/core/repository/Foo.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package um.tesoreria.core.repository;

                public interface Foo {

                    @Modifying
                    @Query("DELETE FROM Foo f WHERE f.id IN :ids")
                    void deleteAllByIdIn(List<Long> ids);
                }
                """);

        List<AuditCoverageGuard.Violation> violations = AuditCoverageGuard.scan(tempDir);

        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().pattern()).isEqualTo("@Query(DML)");
        assertThat(violations.getFirst().line()).isEqualTo(6);
    }

    @Test
    void unaQueryDeLecturaNoEsUnaViolacion(@TempDir Path tempDir) throws IOException {
        Path source = tempDir.resolve("um/tesoreria/core/repository/Foo.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package um.tesoreria.core.repository;

                public interface Foo {

                    @Query("SELECT f FROM Foo f WHERE f.anho = :anho")
                    java.util.List<Object> findAllByAnho(Integer anho);
                }
                """);

        assertThat(AuditCoverageGuard.scan(tempDir)).isEmpty();
    }

    @Test
    void jdbcDeLecturaNoEsUnaViolacionPeroDeEscrituraSi(@TempDir Path tempDir) throws IOException {
        Path read = tempDir.resolve("um/tesoreria/core/adapter/ReadAdapter.java");
        Files.createDirectories(read.getParent());
        Files.writeString(read, """
                package um.tesoreria.core.adapter;

                public class ReadAdapter {
                    private final JdbcTemplate jdbcTemplate;

                    public Integer count() {
                        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tabla", Integer.class);
                    }
                }
                """);

        assertThat(AuditCoverageGuard.scan(tempDir)).isEmpty();

        Path write = tempDir.resolve("um/tesoreria/core/adapter/WriteAdapter.java");
        Files.writeString(write, """
                package um.tesoreria.core.adapter;

                public class WriteAdapter {
                    private final JdbcTemplate jdbcTemplate;

                    public void actualizar() {
                        jdbcTemplate.update("UPDATE tabla SET x = 1");
                    }
                }
                """);

        List<AuditCoverageGuard.Violation> violations = AuditCoverageGuard.scan(tempDir);

        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().pattern()).isEqualTo("JdbcTemplate");
        assertThat(violations.getFirst().file()).isEqualTo(Path.of("um/tesoreria/core/adapter/WriteAdapter.java"));
    }

    @Test
    void ignoraLosComentariosDeLineaYDeBloque(@TempDir Path tempDir) throws IOException {
        Path source = tempDir.resolve("um/tesoreria/core/repository/Commented.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package um.tesoreria.core.repository;

                // Un borrado con @Modifying no dispara post-delete
                /*
                 * Tampoco executeUpdate ni createNativeQuery.
                 */
                public interface Commented {
                    void findAllByAnho(Integer anho);
                }
                """);

        assertThat(AuditCoverageGuard.scan(tempDir)).isEmpty();
    }

    @Test
    void detectaKotlinConQueryDeEscritura(@TempDir Path tempDir) throws IOException {
        Path source = tempDir.resolve("um/tesoreria/core/repository/Foo.kt");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package um.tesoreria.core.repository

                interface Foo {
                    @Query("delete from Foo f where f.anho = :anho")
                    fun deleteAllByAnho(anho: Int)
                }
                """);

        List<AuditCoverageGuard.Violation> violations = AuditCoverageGuard.scan(tempDir);

        assertThat(violations).hasSize(1);
        assertThat(violations.getFirst().pattern()).isEqualTo("@Query(DML)");
    }

    private Set<String> keys(List<AuditCoverageGuard.Violation> violations) {
        return violations.stream()
                .map(AuditCoverageGuard::key)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<String, String> readExceptions() throws IOException {
        if (!Files.exists(EXCEPTIONS)) {
            return Map.of();
        }
        Map<String, String> exceptions = new java.util.LinkedHashMap<>();
        for (String line : Files.readAllLines(EXCEPTIONS)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String[] parts = trimmed.split("\\|", 3);
            if (parts.length < 3 || parts[2].isBlank()) {
                throw new IllegalStateException("Excepción sin motivo: " + trimmed);
            }
            exceptions.put(parts[0] + "|" + parts[1], parts[2]);
        }
        return exceptions;
    }

    private void writeExceptions(Set<String> entries) throws IOException {
        Map<String, String> previous = readExceptions();
        StringBuilder content = new StringBuilder("""
                # Escrituras fuera de JPA exceptuadas con motivo.
                # Generado con: mvn test -Dtest=AuditCoverageGuardTest -Daudit.exceptions.write=true
                # Formato: archivo|patrón|motivo. Si el patrón es Jdbc*, el archivo debe llamar a
                # AuditManualChange y emitir el CHANGE en la misma transacción.
                """);
        entries.stream().sorted().forEach(entry -> content.append(entry)
                .append('|')
                .append(previous.getOrDefault(entry, "PENDIENTE: justificar"))
                .append('\n'));
        Files.writeString(EXCEPTIONS, content.toString());
    }
}
