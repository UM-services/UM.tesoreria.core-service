package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.mapper.ArticuloMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.dependencias.ubicacion.infrastructure.persistence.mapper.UbicacionMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.service.UbicacionArticuloService;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases.GetAllUbicacionArticulosUseCaseImpl;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases.GetUbicacionArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases.GetUbicacionArticulosByArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases.SaveUbicacionArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchRuntimeException;

/**
 * Asignación de ubicación y cuenta a un artículo (#405) contra la base de desarrollo (MySQL real) sin escrituras
 * persistentes: la única conexión crea al abrirse una copia TEMPORARY vacía de {@code ubicacion_articulo} que oculta
 * la real solo en esa sesión. Ubicación, artículo y cuentas se leen de las tablas reales.
 * La copia conserva el índice único (ubicacion_id, articulo_id) pero no las FK: el 1062 es real; 1452 no se reproduce.
 * Sin transacción de la prueba: cada llamada abre y confirma la suya (sobre la copia temporal).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({UbicacionArticuloService.class, SaveUbicacionArticuloUseCaseImpl.class, GetAllUbicacionArticulosUseCaseImpl.class,
        GetUbicacionArticuloUseCaseImpl.class, GetUbicacionArticulosByArticuloUseCaseImpl.class,
        JpaUbicacionArticuloRepositoryAdapter.class, UbicacionArticuloMapper.class, UbicacionMapper.class,
        ArticuloMapper.class, CuentaMapper.class, UbicacionArticuloDevDbIT.Auditoria.class})
@Slf4j
class UbicacionArticuloDevDbIT {

    static final Set<String> TABLAS_TEMPORALES = Set.of("ubicacion_articulo");
    static final List<String> SQL = new CopyOnWriteArrayList<>();

    @TestConfiguration
    @EnableJpaAuditing
    static class Auditoria {
    }

    @DynamicPropertySource
    static void devDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowMultiQueries=true&connectTimeout=5000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium")));
        registry.add("spring.datasource.username", () -> System.getenv("IT_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("IT_DB_PASSWORD"));
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "1");
        registry.add("spring.datasource.hikari.connection-init-sql", () ->
                "CREATE TEMPORARY TABLE IF NOT EXISTS it405_ubicacion_articulo LIKE ubicacion_articulo"
                        + "; CREATE TEMPORARY TABLE IF NOT EXISTS ubicacion_articulo LIKE it405_ubicacion_articulo");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.session_factory.statement_inspector",
                SoloTablasTemporales.class::getName);
    }

    /** Rechaza INSERT/UPDATE/DELETE de Hibernate sobre tablas reales y registra el SQL para revisar el bloqueo. */
    public static class SoloTablasTemporales implements StatementInspector {

        private static final Pattern ESCRITURA = Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete\\s+from)\\s+`?(\\w+)`?", Pattern.CASE_INSENSITIVE);

        @Override
        public String inspect(String sql) {
            var m = ESCRITURA.matcher(sql);
            if (m.find() && !TABLAS_TEMPORALES.contains(m.group(1).toLowerCase(Locale.ROOT))) {
                throw new IllegalStateException("El IT intentó escribir en una tabla real de dev: " + m.group(1));
            }
            SQL.add(sql.toLowerCase(Locale.ROOT));
            return sql;
        }
    }

    @Autowired UbicacionArticuloService service;
    @Autowired JpaUbicacionArticuloRepository jpaRepository;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager txManager;

    Integer ubicacionId;
    List<Long> articulos;
    List<BigDecimal> cuentas;

    @BeforeEach
    void datosRealesYTablaTemporal() {
        for (var tabla : TABLAS_TEMPORALES) {
            var ddl = jdbc.queryForMap("SHOW CREATE TABLE " + tabla).get("Create Table").toString();
            assertThat(ddl).as(tabla + " debe ser la copia TEMPORARY").startsWith("CREATE TEMPORARY TABLE");
        }
        ubicacionId = jdbc.queryForObject("SELECT ubicacion_id FROM ubicacion ORDER BY ubicacion_id LIMIT 1", Integer.class);
        articulos = jdbc.queryForList("SELECT Art_ID FROM articulos ORDER BY Art_ID LIMIT 4", Long.class);
        cuentas = jdbc.queryForList("SELECT pla_cuenta FROM plancta ORDER BY pla_cuenta LIMIT 2", BigDecimal.class);
        SQL.clear();
    }

    @Test
    void asignacionRepetida_dejaUnaFila_actualizaLaCuenta_yLaRespuestaTraeLaNueva() {
        var articuloId = articulos.get(0);
        service.save(asignacion(articuloId, cuentas.get(0)));
        SQL.clear();

        var guardada = service.save(asignacion(articuloId, cuentas.get(1)));

        assertThat(filas(articuloId)).containsExactly(cuentas.get(1));
        assertThat(guardada.getNumeroCuenta()).isEqualByComparingTo(cuentas.get(1));
        assertThat(guardada.getCuenta().getNumeroCuenta()).as("cuenta releída, no la asociación vieja")
                .isEqualByComparingTo(cuentas.get(1));
        assertThat(guardada.getArticulo().getArticuloId()).isEqualTo(articuloId);
        assertThat(guardada.getUbicacion().getUbicacionId()).isEqualTo(ubicacionId);
        assertThat(primeroAntesQue("for update", "update ubicacion_articulo")).as(SQL.toString()).isTrue();
    }

    @Test
    void asignacionNueva_noBloqueaNada_yTraeLasAsociaciones() {
        var articuloId = articulos.get(1);

        var guardada = service.save(asignacion(articuloId, cuentas.get(0)));

        assertThat(SQL).noneMatch(s -> s.contains("for update"));
        assertThat(filas(articuloId)).containsExactly(cuentas.get(0));
        assertThat(guardada.getCuenta().getNumeroCuenta()).isEqualByComparingTo(cuentas.get(0));
        assertThat(guardada.getArticulo().getArticuloId()).isEqualTo(articuloId);
    }

    @Test
    void asignacionConCuentaNula_reemplazaLaCuenta() {
        var articuloId = articulos.get(2);
        service.save(asignacion(articuloId, cuentas.get(0)));

        var guardada = service.save(asignacion(articuloId, null));

        assertThat(filas(articuloId)).containsExactly((BigDecimal) null);
        assertThat(guardada.getCuenta()).isNull();
    }

    @Test
    void insercionDuplicadaReal_seTraduceAConflictoReintentable() {
        var articuloId = articulos.get(3);
        service.save(asignacion(articuloId, cuentas.get(0)));

        // Lo que pasa cuando otra transacción insertó el par entre la búsqueda y el insert
        var ex = catchRuntimeException(() -> new TransactionTemplate(txManager).executeWithoutResult(s -> {
            var duplicado = new UbicacionArticuloEntity();
            duplicado.setUbicacionId(ubicacionId);
            duplicado.setArticuloId(articuloId);
            jpaRepository.saveAndFlush(duplicado);
        }));

        assertThat(UbicacionArticuloRestricciones.codigoMysql(ex)).isEqualTo(UbicacionArticuloRestricciones.CLAVE_DUPLICADA);
        assertThat(UbicacionArticuloRestricciones.nombreRestriccion(ex)).isEqualTo(UbicacionArticuloRestricciones.UNICO_PAR);
        assertThat(UbicacionArticuloRestricciones.traducir(ex, ubicacionId, articuloId))
                .isInstanceOfSatisfying(UbicacionArticuloConflictException.class, c -> assertThat(c.isReintentable()).isTrue());
        assertThat(filas(articuloId)).hasSize(1);
    }

    @Test
    void restriccionesDeDev_coincidenConLasDelTraductor() {
        // information_schema muestra las tablas reales, no las TEMPORARY
        var fks = jdbc.queryForList("""
                SELECT CONSTRAINT_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME FROM information_schema.KEY_COLUMN_USAGE
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ubicacion_articulo' AND REFERENCED_TABLE_NAME IS NOT NULL""");
        var porNombre = fks.stream().collect(Collectors.toMap(
                r -> r.get("CONSTRAINT_NAME").toString(),
                r -> r.get("COLUMN_NAME") + "->" + r.get("REFERENCED_TABLE_NAME")));
        assertThat(porNombre).containsOnlyKeys(UbicacionArticuloRestricciones.CAMPO_POR_FK.keySet());
        assertThat(porNombre).containsEntry("ubicacion_articulo_ibfk_1", "ubicacion_id->ubicacion")
                .containsEntry("ubicacion_articulo_ibfk_2", "articulo_id->articulos")
                .containsEntry("ubicacion_articulo_ibfk_3", "cuenta_contable->plancta");

        var unico = jdbc.queryForList("""
                SELECT COLUMN_NAME FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ubicacion_articulo' AND INDEX_NAME = ? AND NON_UNIQUE = 0
                ORDER BY SEQ_IN_INDEX""", String.class, UbicacionArticuloRestricciones.UNICO_PAR);
        assertThat(unico).containsExactly("ubicacion_id", "articulo_id");
    }

    private UbicacionArticulo asignacion(Long articuloId, BigDecimal cuenta) {
        return UbicacionArticulo.builder().ubicacionId(ubicacionId).articuloId(articuloId).numeroCuenta(cuenta).build();
    }

    private List<BigDecimal> filas(Long articuloId) {
        return jdbc.queryForList("SELECT cuenta_contable FROM ubicacion_articulo WHERE ubicacion_id = ? AND articulo_id = ?",
                BigDecimal.class, ubicacionId, articuloId);
    }

    /** El primer SQL que contiene {@code primero} aparece antes que el primero que contiene {@code despues}. */
    private static boolean primeroAntesQue(String primero, String despues) {
        int i = -1, j = -1;
        for (int k = 0; k < SQL.size(); k++) {
            if (i < 0 && SQL.get(k).contains(primero)) i = k;
            if (j < 0 && SQL.get(k).contains(despues)) j = k;
        }
        return i >= 0 && j >= 0 && i < j;
    }
}
