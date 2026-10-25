package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.repository;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.CreateBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.DeleteBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.UpdateBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaUsoAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.JpaBancariaRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.mapper.BancariaMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Persistencia de bancaria contra MySQL 5.7 de dev. Una sola conexión, tablas TEMPORARY vacías que ocultan las
 * reales (bancaria, plancta, valores, libro_banco e historial) y un inspector que prohíbe escrituras fuera de esas
 * copias. Las copias LIKE no conservan FK: la clasificación real de una FK rota (1451/1452 como FOREIGN_KEY) se
 * verifica en BancariaDevE2E sobre las tablas reales. La cuenta contable y el proveedor se simulan (mock del puerto).
 * Se corre a mano con IT_DB_* (.conductor/run-it.sh); no va a CI.
 */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateBancariaUseCaseImpl.class, UpdateBancariaUseCaseImpl.class, DeleteBancariaUseCaseImpl.class,
        JpaBancariaRepositoryAdapter.class, BancariaMapper.class, BancariaUsoAdapter.class, BancariaHistorialAdapter.class,
        EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class, BancariaDevDbIT.Config.class})
class BancariaDevDbIT {
    static final Set<String> TEMPORALES = Set.of("bancaria", "plancta", "valores", "libro_banco", "gestion_escritura_historial");
    static final List<String> SQL = new CopyOnWriteArrayList<>();
    static final BigDecimal CUENTA = new BigDecimal("11101010001");

    @TestConfiguration
    @EnableJpaAuditing
    static class Config {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-10-23T15:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired CreateBancariaUseCaseImpl crear;
    @Autowired UpdateBancariaUseCaseImpl editar;
    @Autowired DeleteBancariaUseCaseImpl borrar;
    @Autowired JdbcTemplate jdbc;
    @Autowired JpaValorReferenciaRepository valores;
    @Autowired JpaLibroBancoReferenciaRepository libroBanco;
    @MockitoBean BancariaReferenciasPort referencias;

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
                "CREATE TEMPORARY TABLE it409_" + t + " LIKE " + t
                        + "; CREATE TEMPORARY TABLE " + t + " LIKE it409_" + t).collect(Collectors.joining("; ")));
        r.add("spring.jpa.database-platform", () -> "um.tesoreria.core.configuration.MySQL57Dialect");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        r.add("spring.jpa.properties.hibernate.session_factory.statement_inspector", SoloTemporales.class::getName);
    }

    @BeforeEach
    void limpiar() {
        for (String tabla : TEMPORALES) {
            assertThat(jdbc.queryForMap("SHOW CREATE TABLE " + tabla).get("Create Table").toString())
                    .startsWith("CREATE TEMPORARY TABLE");
            jdbc.update("DELETE FROM " + tabla);
        }
        when(referencias.existeCuenta(any())).thenReturn(true);
        when(referencias.existeProveedor(any())).thenReturn(true);
        SQL.clear();
    }

    Bancaria nueva(String titular) {
        return crear.createBancaria(BancariaCambios.builder().banco(Campo.de("Santander")).numero(Campo.de("123-4"))
                .titular(Campo.de(titular)).cuit(Campo.de("30518594466")).numeroCuenta(Campo.de(CUENTA)).build());
    }

    @Test
    void altaEdicionYBajaContraMysqlConUpdatedRelevadoYHistorial() {
        Bancaria creada = nueva("Universidad");
        assertThat(creada.getBancariaId()).isPositive();
        assertThat(creada.getUpdated()).as("updated lo asigna MySQL y se relee").isNotNull();
        assertThat(creada.getCreated()).hasToString("2026-10-23T15:00");
        assertThat(jdbc.queryForObject("SELECT CuB_CUIT FROM bancaria WHERE CuB_ID = ?", String.class, creada.getBancariaId()))
                .isEqualTo("30-51859446-6");
        assertThat(jdbc.queryForObject("SELECT CuB_Prv_ID FROM bancaria WHERE CuB_ID = ?", Integer.class, creada.getBancariaId())).isNull();
        assertThat(jdbc.queryForObject("SELECT uuid FROM bancaria WHERE CuB_ID = ?", String.class, creada.getBancariaId()))
                .matches("[0-9a-f]{32}");

        Bancaria editada = editar.updateBancaria(creada.getBancariaId(), BancariaCambios.builder()
                .titular(Campo.de("Otro titular")).proveedorId(Campo.de(7)).build());
        assertThat(editada.getTitular()).isEqualTo("Otro titular");
        assertThat(editada.getUpdated()).isNotNull();
        assertThat(jdbc.queryForObject("SELECT CuB_Banco FROM bancaria WHERE CuB_ID = ?", String.class, creada.getBancariaId())).isEqualTo("Santander");
        assertThat(jdbc.queryForObject("SELECT CuB_Prv_ID FROM bancaria WHERE CuB_ID = ?", Integer.class, creada.getBancariaId())).isEqualTo(7);
        borrar.deleteBancaria(creada.getBancariaId());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM bancaria", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT GROUP_CONCAT(operacion ORDER BY escritura_historial_id) FROM gestion_escritura_historial", String.class))
                .isEqualTo("ALTA,EDICION,BAJA");
    }

    @Test
    void elTextoDeCp1252SeGuardaYSeLeeIgualEnLaColumnaLatin1() {
        String titular = "Ñandú Peña € “ok”";
        Bancaria creada = nueva(titular);
        assertThat(jdbc.queryForObject("SELECT CuB_Titular FROM bancaria WHERE CuB_ID = ?", String.class, creada.getBancariaId()))
                .isEqualTo(titular);
        // Bytes reales en la columna latin1 (cp1252): Ñ = D1, € = 80.
        assertThat(jdbc.queryForObject("SELECT HEX(CuB_Titular) FROM bancaria WHERE CuB_ID = ?", String.class, creada.getBancariaId()))
                .startsWith("D1").contains("80");
    }

    @Test
    void elEspacioFinalSeGuardaTalCual() {
        Bancaria creada = crear.createBancaria(BancariaCambios.builder().banco(Campo.de("Caja Moneda Extranjera "))
                .numeroCuenta(Campo.de(CUENTA)).build());
        assertThat(jdbc.queryForObject("SELECT HEX(CuB_Banco) FROM bancaria WHERE CuB_ID = ?", String.class, creada.getBancariaId()))
                .endsWith("20");
    }

    @Test
    void laLecturaConLockEsUnForUpdateSimpleEnMysql57() {
        Bancaria creada = nueva("Universidad");
        SQL.clear();
        editar.updateBancaria(creada.getBancariaId(), BancariaCambios.builder().titular(Campo.de("Otro")).build());
        assertThat(SQL.getFirst()).contains("from bancaria", "cub_id=?", "for update").doesNotContain("for update of");
        SQL.clear();
        borrar.deleteBancaria(creada.getBancariaId());
        assertThat(SQL.getFirst()).contains("from bancaria", "cub_id=?", "for update").doesNotContain("for update of");
    }

    @Test
    void usoCuentaValoresYMovimientosDeLibroBancoPorLasEntidadesDeLectura() {
        Bancaria creada = nueva("Universidad");
        Long id = creada.getBancariaId();
        assertThat(valores.existsByBancariaIdOrigen(id)).isFalse();
        assertThat(libroBanco.existsByBancariaId(id)).isFalse();
        jdbc.update("INSERT INTO valores (Val_CuentaOrigen, Val_Estado) VALUES (?, 'Creado'), (?, 'Emitido')", id, id);
        jdbc.update("INSERT INTO libro_banco (LiB_CuB_ID, LiB_Fecha, LiB_Orden, LiB_Tipo) VALUES (?, '2026-10-23', 1, 'Cheque')", id);
        assertThat(valores.existsByBancariaIdOrigen(id)).isTrue();
        assertThat(valores.countByBancariaIdOrigen(id)).isEqualTo(2);
        assertThat(libroBanco.existsByBancariaId(id)).isTrue();
        assertThat(libroBanco.countByBancariaId(id)).isEqualTo(1);
        assertThat(new BancariaUso(2, 1).eliminable()).isFalse();
    }

    @Test
    void lasConsultasDeUsoUsanIndice() {
        assertThat(explain("SELECT Val_ID FROM valores WHERE Val_CuentaOrigen = 8 LIMIT 1")).as("valores").isEqualTo("Val_CuentaOrigen");
        assertThat(explain("SELECT LiB_ID FROM libro_banco WHERE LiB_CuB_ID = 8 LIMIT 1")).as("libro_banco").isNotNull();
    }

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
                throw new IllegalStateException("Escritura fuera de las tablas TEMPORARY de bancaria: " + matcher.group(1));
            }
            SQL.add(sql.toLowerCase(Locale.ROOT));
            return sql;
        }
    }
}
