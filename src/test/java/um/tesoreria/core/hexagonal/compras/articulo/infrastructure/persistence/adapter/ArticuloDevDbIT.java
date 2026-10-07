package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.CreateArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.DeleteArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.UpdateArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.CreateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.DeleteArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.UpdateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.mapper.ArticuloMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.application.usecases.GetCuentaByNumeroCuentaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository.JpaCuentaRepositoryAdapter;
import um.tesoreria.core.service.view.CuentaSearchService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Escrituras de artículo (#405) contra la base de desarrollo (MySQL real) sin escrituras persistentes: la única
 * conexión crea al abrirse una copia TEMPORARY vacía de {@code articulos} que oculta la real solo en esa sesión.
 * {@link SoloTablasTemporales} hace fallar cualquier escritura de Hibernate sobre otra tabla.
 * Las copias TEMPORARY conservan la clave primaria pero no las FK: el 1062 es real; 1451/1452 no se reproducen acá.
 * Sin transacción de la prueba: cada caso de uso abre y confirma la suya (sobre la copia temporal).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateArticuloUseCaseImpl.class, UpdateArticuloUseCaseImpl.class, DeleteArticuloUseCaseImpl.class,
        JpaArticuloRepositoryAdapter.class, ArticuloMapper.class, CuentaMapper.class, ArticuloDevDbIT.Auditoria.class,
        GetCuentaByNumeroCuentaUseCaseImpl.class, JpaCuentaRepositoryAdapter.class, CuentaSearchService.class})
@Slf4j
class ArticuloDevDbIT {

    static final Set<String> TABLAS_TEMPORALES = Set.of("articulos");
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
                "CREATE TEMPORARY TABLE IF NOT EXISTS it405_articulos LIKE articulos"
                        + "; CREATE TEMPORARY TABLE IF NOT EXISTS articulos LIKE it405_articulos");
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

    @Autowired CreateArticuloUseCase crear;
    @Autowired UpdateArticuloUseCase editar;
    @Autowired DeleteArticuloUseCase borrar;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void soloTablasTemporales() {
        for (var tabla : TABLAS_TEMPORALES) {
            var ddl = jdbc.queryForMap("SHOW CREATE TABLE " + tabla).get("Create Table").toString();
            assertThat(ddl).as(tabla + " debe ser la copia TEMPORARY").startsWith("CREATE TEMPORARY TABLE");
        }
        SQL.clear();
    }

    @Test
    void alta_conIdExistente_409YLaFilaQuedaIntacta() {
        crear.createArticulo(gasto(900_001L, "ORIGINAL"));

        assertThatThrownBy(() -> crear.createArticulo(gasto(900_001L, "PISADO")))
                .isInstanceOfSatisfying(ArticuloConflictException.class,
                        ex -> assertThat(ex.getMotivo()).isEqualTo(ArticuloConflictException.Motivo.ID_DUPLICADO));

        assertThat(jdbc.queryForList("SELECT Art_Nombre FROM articulos WHERE Art_ID = 900001", String.class))
                .containsExactly("ORIGINAL");
    }

    @Test
    void edicion_sinCampos_conserva_yConCeroExplicito_aplica() {
        crear.createArticulo(gasto(900_002L, "GASTO"));
        var created = jdbc.queryForObject("SELECT created FROM articulos WHERE Art_ID = 900002", Object.class);

        var editado = editar.updateArticulo(900_002L, Articulo.builder().nombre("GASTO EDITADO").build());

        assertThat(editado.getNombre()).isEqualTo("GASTO EDITADO");
        assertThat(fila(900_002L)).containsExactly("GASTO EDITADO", "gasto", 1, 1, "DESC", "UN");
        assertThat(jdbc.queryForObject("SELECT created FROM articulos WHERE Art_ID = 900002", Object.class))
                .as("created no cambia").isEqualTo(created);

        editar.updateArticulo(900_002L, Articulo.builder().habilitado((byte) 0).build());
        assertThat(fila(900_002L)).containsExactly("GASTO EDITADO", "gasto", 1, 0, "DESC", "UN");
    }

    @Test
    void edicionYBaja_bloqueanLaFilaAntesDeEscribir() {
        crear.createArticulo(gasto(900_003L, "BLOQUEO"));

        SQL.clear();
        editar.updateArticulo(900_003L, Articulo.builder().nombre("BLOQUEO 2").build());
        assertThat(primeroAntesQue("for update", "update articulos")).as(SQL.toString()).isTrue();

        SQL.clear();
        borrar.deleteArticulo(900_003L);
        assertThat(primeroAntesQue("for update", "delete from articulos")).as(SQL.toString()).isTrue();
    }

    @Test
    void edicionYBaja_deInexistente_noEncontrado_sinBloquear() {
        SQL.clear();
        assertThatThrownBy(() -> editar.updateArticulo(900_404L, Articulo.builder().nombre("X").build()))
                .isInstanceOf(ArticuloException.class);
        assertThatThrownBy(() -> borrar.deleteArticulo(900_404L)).isInstanceOf(ArticuloException.class);
        assertThat(SQL).noneMatch(s -> s.contains("for update"));
    }

    @Test
    void baja_libre_borraLaFila() {
        crear.createArticulo(gasto(900_005L, "LIBRE"));

        borrar.deleteArticulo(900_005L);

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM articulos WHERE Art_ID = 900005", Integer.class)).isZero();
    }

    @Test
    void cuentaInexistenteEnPlancta_400SinEscribir_enAltaYEdicion() {
        var inexistente = new BigDecimal("99999999998");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta WHERE pla_cuenta = ?", Integer.class, inexistente))
                .as("la cuenta de la prueba no debe existir en dev").isZero();
        var alta = gasto(900_006L, "SIN CUENTA");
        alta.setNumeroCuenta(inexistente);

        SQL.clear();
        assertThatThrownBy(() -> crear.createArticulo(alta))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("numeroCuenta"));
        assertThat(SQL).as("la validación frena antes de la base").noneMatch(sql -> sql.startsWith("insert"));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM articulos WHERE Art_ID = 900006", Integer.class)).isZero();

        // Con una cuenta real el alta pasa; la edición a una inexistente se frena sin bloquear ni escribir
        var real = jdbc.queryForObject("SELECT MIN(pla_cuenta) FROM plancta", BigDecimal.class);
        alta.setNumeroCuenta(real);
        crear.createArticulo(alta);
        SQL.clear();
        assertThatThrownBy(() -> editar.updateArticulo(900_006L, Articulo.builder().numeroCuenta(inexistente).build()))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isEqualTo("numeroCuenta"));
        assertThat(SQL).noneMatch(sql -> sql.contains("for update") || sql.startsWith("update"));
        assertThat(jdbc.queryForObject("SELECT Art_Cuenta FROM articulos WHERE Art_ID = 900006", BigDecimal.class))
                .isEqualByComparingTo(real);
    }

    @Test
    void restriccionesDeDev_coincidenConLasDelTraductor() {
        // information_schema muestra las tablas reales, no las TEMPORARY
        var fks = jdbc.queryForList("""
                SELECT CONSTRAINT_NAME, TABLE_NAME, REFERENCED_TABLE_NAME FROM information_schema.KEY_COLUMN_USAGE
                WHERE TABLE_SCHEMA = DATABASE() AND REFERENCED_TABLE_NAME IS NOT NULL
                  AND (TABLE_NAME = 'articulos' OR REFERENCED_TABLE_NAME = 'articulos')""");
        var porNombre = fks.stream().collect(Collectors.toMap(
                r -> r.get("CONSTRAINT_NAME").toString(),
                r -> r.get("TABLE_NAME") + "->" + r.get("REFERENCED_TABLE_NAME")));

        assertThat(porNombre).containsEntry("articulos_ibfk_1", "articulos->plancta");
        assertThat(porNombre).as("FK que referencian a articulos: una nueva cae en la respuesta genérica")
                .containsOnlyKeys(Set.of("articulos_ibfk_1", "entrega_detalle_ibfk_2", "ubicacion_articulo_ibfk_2"));
        ArticuloRestricciones.TABLA_POR_FK.forEach((fk, tabla) ->
                assertThat(porNombre).containsEntry(fk, tabla + "->articulos"));
        ArticuloRestricciones.CAMPO_POR_FK.keySet().forEach(fk -> assertThat(porNombre).containsKey(fk));

        log.info("@@sql_mode de dev: {}", jdbc.queryForObject("SELECT @@sql_mode", String.class));
        log.info("versión de MySQL de dev: {}", jdbc.queryForObject("SELECT VERSION()", String.class));
    }

    private static Articulo gasto(Long id, String nombre) {
        return Articulo.builder().articuloId(id).nombre(nombre).descripcion("DESC").unidad("UN")
                .precio(BigDecimal.ONE).tipo("gasto").directo((byte) 1).habilitado((byte) 1).build();
    }

    private List<Object> fila(Long id) {
        var r = jdbc.queryForMap("SELECT Art_Nombre, Art_Tipo, Art_Directo + 0 AS Art_Directo, Art_Habilitado + 0 AS Art_Habilitado, Art_Descripcion, Art_Unidad FROM articulos WHERE Art_ID = ?", id);
        return List.of(r.get("Art_Nombre"), r.get("Art_Tipo"), ((Number) r.get("Art_Directo")).intValue(),
                ((Number) r.get("Art_Habilitado")).intValue(), r.get("Art_Descripcion"), r.get("Art_Unidad"));
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
