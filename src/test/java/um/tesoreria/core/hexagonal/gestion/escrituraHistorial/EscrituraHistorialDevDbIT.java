package um.tesoreria.core.hexagonal.gestion.escrituraHistorial;

import jakarta.persistence.EntityManager;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.adapter.JpaProveedorRepositoryAdapter;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.mapper.ProveedorMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Historial de escrituras (#404) contra la base de desarrollo (MySQL real), sin escrituras persistentes:
 * una sola conexión que al abrirse crea, como TEMPORARY, la tabla del script V404 y una copia
 * vacía de {@code proveedores} que oculta la real solo en esa sesión (también si la real existe).
 * {@link SoloTablasTemporales} hace fallar cualquier escritura de Hibernate sobre otra tabla.
 * Requiere las variables {@code IT_DB_*} y permiso {@code CREATE TEMPORARY TABLES}; ver README.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Import({EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class,
        JpaProveedorRepositoryAdapter.class, ProveedorMapper.class, CuentaMapper.class})
class EscrituraHistorialDevDbIT {

    /** Tablas que la conexión oculta con una copia TEMPORARY: las únicas donde se puede escribir. */
    static final Set<String> TABLAS_TEMPORALES = Set.of("gestion_escritura_historial", "proveedores");

    @DynamicPropertySource
    static void devDb(DynamicPropertyRegistry registry) throws Exception {
        var ddl = Files.readAllLines(Path.of("docs/sql/V404__gestion_escritura_historial.sql")).stream()
                .filter(l -> !l.trim().startsWith("--"))
                .collect(Collectors.joining(" "))
                .replace("CREATE TABLE IF NOT EXISTS", "CREATE TEMPORARY TABLE IF NOT EXISTS")
                .trim();
        if (ddl.endsWith(";")) ddl = ddl.substring(0, ddl.length() - 1);
        // Un cambio en el script no puede convertir esto en DDL permanente ni sumar sentencias
        if (!ddl.startsWith("CREATE TEMPORARY TABLE IF NOT EXISTS gestion_escritura_historial") || ddl.contains(";")) {
            throw new IllegalStateException("V404 ya no es una única CREATE TABLE IF NOT EXISTS: revisar el IT");
        }
        var initSql = ddl + "; CREATE TEMPORARY TABLE IF NOT EXISTS review404_prov LIKE proveedores"
                + "; CREATE TEMPORARY TABLE IF NOT EXISTS proveedores LIKE review404_prov";

        registry.add("spring.datasource.url", () -> "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowMultiQueries=true&connectTimeout=5000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium")));
        registry.add("spring.datasource.username", () -> System.getenv("IT_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("IT_DB_PASSWORD"));
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "1");
        registry.add("spring.datasource.hikari.connection-init-sql", () -> initSql);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.session_factory.statement_inspector",
                SoloTablasTemporales.class::getName);
    }

    /** Rechaza INSERT/UPDATE/DELETE de Hibernate sobre tablas que no están ocultas por una TEMPORARY. */
    public static class SoloTablasTemporales implements StatementInspector {

        private static final Pattern ESCRITURA = Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete\\s+from)\\s+`?(\\w+)`?", Pattern.CASE_INSENSITIVE);

        @Override
        public String inspect(String sql) {
            var m = ESCRITURA.matcher(sql);
            if (m.find() && !TABLAS_TEMPORALES.contains(m.group(1).toLowerCase(Locale.ROOT))) {
                throw new IllegalStateException("El IT intentó escribir en una tabla real de dev: " + m.group(1));
            }
            return sql;
        }
    }

    @Autowired RegistrarEscrituraHistorialUseCase historial;
    @Autowired JpaProveedorRepositoryAdapter proveedores;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager txManager;
    @Autowired EntityManager entityManager;

    @BeforeEach
    void soloTablasTemporales() {
        for (var tabla : TABLAS_TEMPORALES) {
            var ddl = jdbc.queryForMap("SHOW CREATE TABLE " + tabla).get("Create Table").toString();
            assertThat(ddl).as(tabla + " debe ser la copia TEMPORARY").startsWith("CREATE TEMPORARY TABLE");
        }
    }

    @Test
    void contratoSobreMysqlReal_altaEdicionBaja() {
        historial.registrarAlta("ejercicio", "99", Map.of("nombre", "2026"));
        historial.registrarEdicion("ejercicio", "99", Map.of("nombre", "2026"), Map.of("nombre", "2026-bis"));
        historial.registrarBaja("ejercicio", "99", Map.of("nombre", "2026-bis"));

        var rows = jdbc.queryForList("""
                SELECT operacion, valor_anterior, valor_nuevo, ABS(TIMESTAMPDIFF(SECOND, fecha, NOW())) AS seg_vs_now
                FROM gestion_escritura_historial WHERE entidad='ejercicio' AND entidad_clave='99'
                ORDER BY escritura_historial_id""");
        assertThat(rows).extracting(r -> r.get("operacion")).containsExactly("ALTA", "EDICION", "BAJA");
        assertThat(rows.get(0).get("valor_anterior")).isNull();
        assertThat(rows.get(1).get("valor_anterior")).isEqualTo("{\"nombre\":\"2026\"}");
        assertThat(rows.get(1).get("valor_nuevo")).isEqualTo("{\"nombre\":\"2026-bis\"}");
        assertThat(rows.get(2).get("valor_nuevo")).isNull();
        assertThat(rows).as("fecha = NOW() de la sesión")
                .allSatisfy(r -> assertThat(((Number) r.get("seg_vs_now")).longValue()).isLessThan(60));
        // NOW() es la hora del servidor solo si el driver no cambia la zona de la sesión, igual que en VB6
        assertThat(jdbc.queryForObject("SELECT @@session.time_zone", String.class)).isEqualTo("SYSTEM");
    }

    @Test
    void tablaRealDeDev_coincideConElScript() {
        // information_schema muestra la tabla base, no la TEMPORARY: detecta un script editado después de aplicarse
        var existe = jdbc.queryForObject("""
                SELECT COUNT(*) FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'gestion_escritura_historial'""", Integer.class);
        assumeTrue(existe == 1, "la tabla real todavía no se aplicó en esta base");
        var fecha = jdbc.queryForMap("""
                SELECT COLUMN_DEFAULT, DATETIME_PRECISION, IS_NULLABLE FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'gestion_escritura_historial' AND COLUMN_NAME = 'fecha'""");
        assertThat(fecha.get("COLUMN_DEFAULT")).isEqualTo("CURRENT_TIMESTAMP(6)");
        assertThat(((Number) fecha.get("DATETIME_PRECISION")).intValue()).isEqualTo(6);
        assertThat(fecha.get("IS_NULLABLE")).isEqualTo("NO");
    }

    @Test
    void valoresConAcentosYEnie_seGuardanSinPerderCaracteres() {
        historial.registrarAlta("proveedor", "utf8-1", Map.of("razon", "PEÑA ÁLVAREZ – Cía"));

        var nuevo = jdbc.queryForObject(
                "SELECT valor_nuevo FROM gestion_escritura_historial WHERE entidad='proveedor' AND entidad_clave='utf8-1'",
                String.class);
        assertThat(nuevo).isEqualTo("{\"razon\":\"PEÑA ÁLVAREZ – Cía\"}");
    }

    @Test
    void edicionDeProveedorHexagonal_antesYDespuesReales() {
        var creado = proveedores.create(Proveedor.builder().razonSocial("ORIGINAL SA").cuit("20-00000000-1").build());
        var id = creado.getProveedorId();
        var antes = proveedores.findByProveedorId(id).orElseThrow();
        var cambios = Proveedor.builder().razonSocial("NUEVA SA").cuit("20-00000000-1").build();
        var despues = proveedores.update(id, cambios).orElseThrow();
        historial.registrarEdicion("proveedor", String.valueOf(id), antes, despues);

        var fila = jdbc.queryForMap("SELECT valor_anterior, valor_nuevo FROM gestion_escritura_historial WHERE entidad='proveedor' AND entidad_clave=?", String.valueOf(id));
        assertThat(fila.get("valor_anterior").toString()).contains("ORIGINAL SA").doesNotContain("NUEVA SA");
        assertThat(fila.get("valor_nuevo").toString()).contains("NUEVA SA");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void rollbackDeNegocio_noDejaNiCambioNiEvento() {
        var tx = new TransactionTemplate(txManager);
        Integer id = tx.execute(s -> proveedores.create(Proveedor.builder().razonSocial("ROLLBACK ORIG").build()).getProveedorId());

        assertThatThrownBy(() -> tx.executeWithoutResult(s -> {
            var antes = proveedores.findByProveedorId(id).orElseThrow();
            var despues = proveedores.update(id, Proveedor.builder().razonSocial("ROLLBACK NUEVA").build()).orElseThrow();
            entityManager.flush();
            assertThat(razonEnLaBase(id)).as("el UPDATE llegó a MySQL antes del rollback").isEqualTo("ROLLBACK NUEVA");
            historial.registrarEdicion("proveedor", String.valueOf(id), antes, despues);
            throw new IllegalStateException("negocio rechazado");
        })).isInstanceOf(IllegalStateException.class).hasMessage("negocio rechazado");

        var eventos = jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial WHERE entidad='proveedor' AND entidad_clave=?", Integer.class, String.valueOf(id));
        assertThat(razonEnLaBase(id)).isEqualTo("ROLLBACK ORIG");
        assertThat(eventos).isZero();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void falloDelHistorial_revierteLaEscrituraDeNegocio() {
        var tx = new TransactionTemplate(txManager);
        Integer id = tx.execute(s -> proveedores.create(Proveedor.builder().razonSocial("FALLA ORIG").build()).getProveedorId());
        var claveLarga = "k".repeat(300);

        assertThatThrownBy(() -> tx.executeWithoutResult(s -> {
            var antes = proveedores.findByProveedorId(id).orElseThrow();
            var despues = proveedores.update(id, Proveedor.builder().razonSocial("FALLA NUEVA").build()).orElseThrow();
            entityManager.flush();
            assertThat(razonEnLaBase(id)).as("el UPDATE llegó a MySQL antes del rollback").isEqualTo("FALLA NUEVA");
            historial.registrarEdicion("proveedor", claveLarga, antes, despues);
        })).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("entidadClave supera");

        var eventos = jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial WHERE entidad='proveedor' AND entidad_clave LIKE 'kkk%'", Integer.class);
        assertThat(razonEnLaBase(id)).isEqualTo("FALLA ORIG");
        assertThat(eventos).isZero();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void sinTransaccion_rechazado() {
        assertThatThrownBy(() -> historial.registrarAlta("cuenta", "sin-tx", Map.of("n", 1)))
                .isInstanceOf(IllegalTransactionStateException.class);
    }

    private String razonEnLaBase(Integer id) {
        return jdbc.queryForObject("SELECT Prv_Razon FROM proveedores WHERE Prv_ID=?", String.class, id);
    }
}
