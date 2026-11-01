package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mismos casos de persistencia contra MySQL dev. Una sola conexión, tres tablas TEMPORARY
 * vacías que ocultan las reales y un inspector que prohíbe escrituras fuera de esas copias.
 * Las copias LIKE no conservan FK: las carreras y FK se verifican en el E2E opt-in.
 */
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CuentaDevDbIT extends CuentaPersistenceTest {
    static final Set<String> TEMPORALES = Set.of("plancta", "movcon", "gestion_escritura_historial");
    static final java.util.List<String> SQL = new java.util.concurrent.CopyOnWriteArrayList<>();

    @DynamicPropertySource
    static void dev(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", () -> "jdbc:mysql://%s:%s/%s?sslMode=REQUIRED&serverTimezone=UTC&allowMultiQueries=true&connectTimeout=60000&socketTimeout=300000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium")));
        r.add("spring.datasource.username", () -> System.getenv("IT_DB_USER"));
        r.add("spring.datasource.password", () -> System.getenv("IT_DB_PASSWORD"));
        r.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        r.add("spring.datasource.hikari.maximum-pool-size", () -> "1");
        r.add("spring.datasource.hikari.connection-timeout", () -> "120000");
        r.add("spring.datasource.hikari.connection-init-sql", () -> TEMPORALES.stream().map(t ->
                "CREATE TEMPORARY TABLE it407_" + t + " LIKE " + t
                        + "; CREATE TEMPORARY TABLE " + t + " LIKE it407_" + t).collect(java.util.stream.Collectors.joining("; ")));
        r.add("spring.jpa.database-platform", () -> "um.tesoreria.core.configuration.MySQL57Dialect");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        r.add("spring.jpa.properties.hibernate.session_factory.statement_inspector", SoloTemporales.class::getName);
    }

    @Override
    @BeforeEach
    void limpiar() {
        for (String tabla : TEMPORALES) {
            assertThat(jdbc.queryForMap("SHOW CREATE TABLE " + tabla).get("Create Table").toString())
                    .startsWith("CREATE TEMPORARY TABLE");
        }
        super.limpiar();
    }

    @Test
    void movimientoHuerfanoEnRangoImpideBajaAunqueNoExistaLaCuentaHija() {
        crear.createCuenta(cuenta("10000000000"));
        crear.createCuenta(cuenta("11000000000"));
        movimiento("11000000001"); // LIKE no copia las FK; reproduce el huérfano sin tocar datos reales.
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> borrar.deleteCuenta(new java.math.BigDecimal("11000000000")))
                .isInstanceOf(um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException.class)
                .hasMessageContaining("registraciones");
    }

    @Test
    void altaBloqueaAntesDeLeerMovimientosEnSqlReal() {
        crear.createCuenta(cuenta("10000000000"));
        crear.createCuenta(cuenta("11000000000"));
        SQL.clear();
        crear.createCuenta(cuenta("11010000000"));
        assertThat(SQL.getFirst()).contains("from plancta", "for update");
        int movimiento = java.util.stream.IntStream.range(0, SQL.size())
                .filter(i -> SQL.get(i).contains("from movcon")).findFirst().orElseThrow();
        assertThat(SQL.subList(0, movimiento).stream().filter(sql -> sql.contains("for update")).count()).isEqualTo(3);
    }

    public static class SoloTemporales implements StatementInspector {
        private static final Pattern ESCRITURA = Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete(?:\\s+\\w+)?\\s+from)\\s+`?(\\w+)`?", Pattern.CASE_INSENSITIVE);
        @Override
        public String inspect(String sql) {
            var matcher = ESCRITURA.matcher(sql);
            if (matcher.find() && !TEMPORALES.contains(matcher.group(1).toLowerCase(Locale.ROOT))) {
                throw new IllegalStateException("Escritura fuera de las tablas TEMPORARY de cuenta: " + matcher.group(1));
            }
            SQL.add(sql.toLowerCase(Locale.ROOT));
            return sql;
        }
    }
}
