package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.repository;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository.JpaAsientoRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository.JpaCuentaMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mismos casos de persistencia contra MySQL dev. Una sola conexión, tablas TEMPORARY vacías que ocultan las reales
 * y un inspector que prohíbe escrituras fuera de esas copias. Las copias LIKE no conservan FK: las FK de Compras y
 * las carreras se verifican en el E2E opt-in. Las consultas de uso sobre movcon y asiento son de solo lectura.
 */
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EjercicioDevDbIT extends EjercicioPersistenceTest {
    static final Set<String> TEMPORALES = Set.of("ejercicios", "gestion_escritura_historial");
    static final List<String> SQL = new CopyOnWriteArrayList<>();

    @Autowired JpaAsientoRepository asientos;
    @Autowired JpaCuentaMovimientoRepository movimientos;

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
                "CREATE TEMPORARY TABLE it408_" + t + " LIKE " + t
                        + "; CREATE TEMPORARY TABLE " + t + " LIKE it408_" + t).collect(Collectors.joining("; ")));
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
    void altaYBajaBloqueanElUltimoConForUpdateSimpleEnMysql57() {
        SQL.clear();
        crear.createEjercicio(um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio.builder().build());
        assertThat(SQL.getFirst()).contains("from ejercicios", "order by", "for update").doesNotContain("for update of");
        SQL.clear();
        editar.updateEjercicio(16, um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio.builder()
                .bloqueado((byte) 1).build());
        assertThat(SQL.getFirst()).contains("from ejercicios", "eje_id=?", "for update").doesNotContain("for update of");
    }

    @Test
    void lasConsultasDeUsoUsanIndiceEnMovconYAsiento() {
        // EXPLAIN del mismo rango por fecha que ejecutan existsByFechaContableBetween y existsByFechaBetween.
        assertThat(explain("SELECT id FROM movcon WHERE mco_fecha BETWEEN '2027-03-01 00:00:00' AND '2028-02-29 23:59:59' LIMIT 1"))
                .as("movcon").isNotNull();
        assertThat(explain("SELECT asiento_id FROM asiento WHERE fecha BETWEEN '2027-03-01 00:00:00' AND '2028-02-29 23:59:59' LIMIT 1"))
                .as("asiento").isNotNull();
    }

    @Test
    void movconYAsientoSeConsultanPorFechaEnUtc() {
        OffsetDateTime desde = OffsetDateTime.parse("2020-03-01T00:00:00Z");
        OffsetDateTime hasta = OffsetDateTime.parse("2021-02-28T23:59:59Z");
        assertThat(movimientos.existsByFechaContableBetween(desde, hasta)).isTrue();
        assertThat(asientos.existsByFechaBetween(desde, hasta)).isTrue();
        // Fuera de toda fecha cargada.
        assertThat(movimientos.existsByFechaContableBetween(OffsetDateTime.parse("1901-01-01T00:00:00Z"),
                OffsetDateTime.parse("1901-12-31T23:59:59Z"))).isFalse();
        assertThat(asientos.existsByFechaBetween(OffsetDateTime.parse("1901-01-01T00:00:00Z"),
                OffsetDateTime.parse("1901-12-31T23:59:59Z"))).isFalse();
    }

    /** Devuelve el índice elegido por MySQL para la consulta (null = recorrido completo). */
    private String explain(String sql) {
        List<Map<String, Object>> plan = jdbc.queryForList("EXPLAIN " + sql);
        return (String) plan.getFirst().get("key");
    }

    public static class SoloTemporales implements StatementInspector {
        private static final Pattern ESCRITURA = Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete(?:\\s+\\w+)?\\s+from)\\s+`?(\\w+)`?", Pattern.CASE_INSENSITIVE);

        @Override
        public String inspect(String sql) {
            var matcher = ESCRITURA.matcher(sql);
            if (matcher.find() && !TEMPORALES.contains(matcher.group(1).toLowerCase(Locale.ROOT))) {
                throw new IllegalStateException("Escritura fuera de las tablas TEMPORARY de ejercicio: " + matcher.group(1));
            }
            SQL.add(sql.toLowerCase(Locale.ROOT));
            return sql;
        }
    }
}
