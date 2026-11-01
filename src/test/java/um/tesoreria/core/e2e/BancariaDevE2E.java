package um.tesoreria.core.e2e;

import com.jayway.jsonpath.JsonPath;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.controller.BancariaController;
import um.tesoreria.core.controller.BancariaExceptionAdvice;
import um.tesoreria.core.hexagonal.compras.proveedor.application.usecases.GetProveedorByIdUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.adapter.JpaProveedorRepositoryAdapter;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.mapper.ProveedorMapper;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.CreateBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.DeleteBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.GetBancariaByIdUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.GetBancariaUsoUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.application.usecases.UpdateBancariaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaReferenciasAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.BancariaUsoAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter.JpaBancariaRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.BancariaEntity;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.mapper.BancariaMapper;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.mapper.BancariaDtoMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.application.usecases.GetCuentaByNumeroCuentaUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository.JpaCuentaRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;
import um.tesoreria.core.service.BancariaService;

import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Cuentas bancarias (#409) contra MySQL de desarrollo. Opt-in: escribe en tablas reales.
 * Ejecutar a mano, siempre en local: E2E_DEV_ESCRITURA=si, IT_DB_* y -Dit.test=BancariaDevE2E.
 * <p>
 * Qué es real: MySQL 5.7 de dev (FK RESTRICT de valores y libro_banco, locks, latin1, historial), los casos de uso, los
 * adaptadores JPA, los puertos de cuenta contable y proveedor y el controlador legacy (MockMvc standalone, sin Tomcat).
 * Qué es simulado: nada en los casos de uso. No se prueba con el ejecutable VB6, ni se paga con CompraService (eso
 * escribe asientos y libro banco reales; está cubierto por una unitaria con Mockito).
 * <p>
 * Escribe: cuentas bancarias con banco {@code E2E-409-*}, filas de valores y libro_banco marcadas con ese prefijo
 * (solo para provocar la FK de la baja) y eventos de historial de esas cuentas. Al terminar borra lo propio, restaura
 * los AUTO_INCREMENT e informa (no falla) si el CHECKSUM difiere: dev es compartida. No toca cuentas ajenas. Las
 * concurrencias fuerzan el cruce con una pausa del SQL y comprueban la espera en INNODB_LOCK_WAITS; cada una se repite
 * con un mutante (lectura sin bloqueo) que tiene que perder la carrera.
 */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "E2E_DEV_ESCRITURA", matches = "si")
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateBancariaUseCaseImpl.class, UpdateBancariaUseCaseImpl.class, DeleteBancariaUseCaseImpl.class,
        GetBancariaByIdUseCaseImpl.class, GetBancariaUsoUseCaseImpl.class, JpaBancariaRepositoryAdapter.class,
        BancariaMapper.class, BancariaUsoAdapter.class, BancariaHistorialAdapter.class, BancariaReferenciasAdapter.class,
        GetCuentaByNumeroCuentaUseCaseImpl.class, JpaCuentaRepositoryAdapter.class, CuentaMapper.class,
        um.tesoreria.core.service.view.CuentaSearchService.class,
        GetProveedorByIdUseCaseImpl.class, JpaProveedorRepositoryAdapter.class, ProveedorMapper.class,
        BancariaDtoMapper.class, BancariaService.class, EscrituraHistorialService.class,
        JpaEscrituraHistorialRepositoryAdapter.class, EscrituraHistorialMapper.class,
        JacksonEscrituraValorSerializer.class, BancariaDevE2E.Config.class})
class BancariaDevE2E {
    static final String PREFIJO = "E2E-409-";
    static final String MARCA = PREFIJO + UUID.randomUUID().toString().substring(0, 8);
    static final String HILO = "bancaria409";
    static final List<String> TABLAS = List.of("bancaria", "gestion_escritura_historial", "valores", "libro_banco");
    static final List<String> CON_AUTO_INCREMENT = List.of("bancaria", "gestion_escritura_historial");
    static final List<String> ESCRIBIBLES = List.of("bancaria", "gestion_escritura_historial");

    static volatile boolean propietario;
    static Map<String, Long> fotoInicial;
    static Map<String, Long> autoIncrementInicial;
    static String cuentaReal;
    static String proveedorReal;
    static volatile Predicate<String> pausarEn;
    static volatile CountDownLatch enPausa;
    static volatile CountDownLatch reanudar;
    static final List<String> SQL = new java.util.concurrent.CopyOnWriteArrayList<>();

    @TestConfiguration
    @EnableJpaAuditing
    static class Config {
        @Bean
        Clock clock() {
            return Clock.systemDefaultZone();
        }
    }

    @Autowired CreateBancariaUseCaseImpl crear;
    @Autowired UpdateBancariaUseCaseImpl editar;
    @Autowired DeleteBancariaUseCaseImpl borrar;
    @Autowired GetBancariaByIdUseCaseImpl obtener;
    @Autowired GetBancariaUsoUseCaseImpl uso;
    @Autowired BancariaRepository repositorio;
    @Autowired BancariaMapper mapper;
    @Autowired BancariaDtoMapper dtoMapper;
    @Autowired BancariaHistorialAdapter historial;
    @Autowired BancariaUsoAdapter usoAdapter;
    @Autowired BancariaReferenciasAdapter referencias;
    @Autowired BancariaService legacy;
    @Autowired TransactionOperations transaction;
    @Autowired EntityManager entityManager;

    MockMvc http;

    static String url() {
        return "jdbc:mysql://%s:%s/%s?sslMode=REQUIRED&serverTimezone=UTC&connectTimeout=60000&socketTimeout=300000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium"));
    }

    @DynamicPropertySource
    static void dev(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", BancariaDevE2E::url);
        r.add("spring.datasource.username", () -> System.getenv("IT_DB_USER"));
        r.add("spring.datasource.password", () -> System.getenv("IT_DB_PASSWORD"));
        r.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        r.add("spring.datasource.hikari.maximum-pool-size", () -> "4");
        r.add("spring.datasource.hikari.connection-init-sql", () -> "SET SESSION innodb_lock_wait_timeout=60");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        r.add("spring.jpa.database-platform", () -> "um.tesoreria.core.configuration.MySQL57Dialect");
        r.add("spring.jpa.properties.hibernate.session_factory.statement_inspector", Inspector.class::getName);
    }

    public static class Inspector implements StatementInspector {
        private static final java.util.regex.Pattern ESCRITURA = java.util.regex.Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete(?:\\s+\\w+)?\\s+from)\\s+`?(\\w+)`?");

        @Override
        public String inspect(String sql) {
            String normal = sql.toLowerCase(Locale.ROOT).stripLeading();
            var escritura = ESCRITURA.matcher(normal);
            if (escritura.find() && !ESCRIBIBLES.contains(escritura.group(1))) {
                throw new IllegalStateException("Escritura fuera del flujo de bancaria: " + escritura.group(1));
            }
            SQL.add(normal);
            var condicion = pausarEn;
            if (condicion != null && Thread.currentThread().getName().equals(HILO) && condicion.test(normal)) {
                pausarEn = null;
                enPausa.countDown();
                try {
                    if (!reanudar.await(10, TimeUnit.MINUTES)) throw new IllegalStateException("La pausa no se liberó");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ex);
                }
            }
            return sql;
        }
    }

    // ---------------------------------------------------------------- ciclo de vida y limpieza

    @BeforeAll
    static void fotografiar() throws Exception {
        try (var db = abrir()) {
            long antes = autoIncrement(db, "bancaria");
            boolean habiaResiduos = contar(db, "SELECT COUNT(*) FROM bancaria WHERE CuB_Banco LIKE '" + PREFIJO + "%'") > 0;
            propietario = true;
            limpiarPropio(db); // residuos marcados de una corrida interrumpida
            if (habiaResiduos) {
                System.out.println("E2E-409 había residuos de una corrida anterior: se borraron");
            }
            cuentaReal = texto(db, "SELECT CuB_Pla_Cuenta FROM bancaria WHERE CuB_Pla_Cuenta IS NOT NULL ORDER BY CuB_ID LIMIT 1");
            proveedorReal = texto(db, "SELECT Prv_ID FROM proveedores ORDER BY Prv_ID LIMIT 1");
            assertThat(cuentaReal).as("hay al menos una cuenta bancaria con cuenta contable").isNotNull();
            assertThat(proveedorReal).as("hay al menos un proveedor").isNotNull();
            fotoInicial = checksums(db);
            autoIncrementInicial = autoIncrements(db);
            if (habiaResiduos) {
                autoIncrementInicial.put("bancaria", contar(db, "SELECT COALESCE(MAX(CuB_ID), 0) + 1 FROM bancaria"));
            } else {
                assertThat(autoIncrementInicial.get("bancaria")).isEqualTo(antes);
            }
        }
    }

    @AfterAll
    static void restaurar() throws Exception {
        if (!propietario || autoIncrementInicial == null) {
            return; // la preparación no llegó a terminar: no se escribió nada
        }
        try (var db = abrir()) {
            try {
                limpiarPropio(db);
            } finally {
                restaurarAutoIncrements(db);
            }
            var autoIncrementFinal = autoIncrements(db);
            var fotoFinal = checksums(db);
            System.out.println("E2E-409 AUTO_INCREMENT como antes: " + autoIncrementFinal.equals(autoIncrementInicial));
            System.out.println("E2E-409 tablas idénticas a antes: " + fotoFinal.equals(fotoInicial)
                    + (fotoFinal.equals(fotoInicial) ? "" : " (difieren: " + fotoInicial + " -> " + fotoFinal + "; ver si otra sesión escribió en dev)"));
            assertThat(contar(db, "SELECT COUNT(*) FROM bancaria WHERE CuB_Banco LIKE '" + PREFIJO + "%'")).as("sin cuentas propias").isZero();
        }
    }

    @BeforeEach
    void preparar() throws Exception {
        assertThat(propietario).as("sin propiedad verificada no se escribe en dev").isTrue();
        try (var db = abrir()) {
            limpiarPropio(db);
        }
        SQL.clear();
        http = MockMvcBuilders.standaloneSetup(new BancariaController(legacy, obtener, uso, crear, editar, borrar, dtoMapper))
                .setControllerAdvice(new BancariaExceptionAdvice()).build();
    }

    @AfterEach
    void soltarYLimpiar() throws Exception {
        pausarEn = null;
        if (reanudar != null) reanudar.countDown();
        if (!propietario) {
            return;
        }
        try (var db = abrir()) {
            limpiarPropio(db);
        }
    }

    static Connection abrir() throws SQLException {
        return DriverManager.getConnection(url(), System.getenv("IT_DB_USER"), System.getenv("IT_DB_PASSWORD"));
    }

    /**
     * Todo lo propio lleva la marca: en el banco (cuentas), el titular (valores), el concepto (libro banco) y, en el
     * historial, dentro del estado guardado (todo evento de una cuenta propia lleva su banco marcado, también el de
     * una cuenta que la prueba ya borró). Nunca se borra por rango de id: dev es compartida.
     */
    static void limpiarPropio(Connection db) throws SQLException {
        for (String sql : List.of(
                "DELETE FROM libro_banco WHERE LiB_Concepto LIKE '" + PREFIJO + "%'",
                "DELETE FROM valores WHERE Val_NombreTitular LIKE '" + PREFIJO + "%'",
                "DELETE FROM gestion_escritura_historial WHERE entidad = 'bancaria' AND (valor_nuevo LIKE '%\"" + "banco\":\"" + PREFIJO
                        + "%' OR valor_anterior LIKE '%\"" + "banco\":\"" + PREFIJO + "%')",
                "DELETE FROM bancaria WHERE CuB_Banco LIKE '" + PREFIJO + "%'")) {
            try (var st = db.createStatement()) {
                st.executeUpdate(sql);
            }
        }
    }

    static long contar(Connection db, String sql) throws SQLException {
        try (var st = db.createStatement(); var rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    static long contar(String sql) throws SQLException {
        try (var db = abrir()) {
            return contar(db, sql);
        }
    }

    static String texto(Connection db, String sql) throws SQLException {
        try (var st = db.createStatement(); var rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    static String texto(String sql) throws SQLException {
        try (var db = abrir()) {
            return texto(db, sql);
        }
    }

    static void ejecutar(String sql) throws SQLException {
        try (var db = abrir(); var st = db.createStatement()) {
            st.executeUpdate(sql);
        }
    }

    static Map<String, Long> checksums(Connection db) throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        for (String tabla : TABLAS) {
            try (var st = db.createStatement(); var rs = st.executeQuery("CHECKSUM TABLE " + tabla)) {
                rs.next();
                r.put(tabla, rs.getLong(2));
            }
        }
        return r;
    }

    static long autoIncrement(Connection db, String tabla) throws SQLException {
        return contar(db, "SELECT auto_increment FROM information_schema.TABLES WHERE table_schema = DATABASE()"
                + " AND table_name = '" + tabla + "'");
    }

    static Map<String, Long> autoIncrements(Connection db) throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        for (String tabla : CON_AUTO_INCREMENT) {
            r.put(tabla, autoIncrement(db, tabla));
        }
        return r;
    }

    static void restaurarAutoIncrements(Connection db) throws SQLException {
        try (var st = db.createStatement()) {
            st.execute("SET SESSION lock_wait_timeout = 5"); // sin trabar a otros: si la tabla está tomada, falla rápido
            var actuales = autoIncrements(db);
            for (var e : autoIncrementInicial.entrySet()) {
                if (!e.getValue().equals(actuales.get(e.getKey()))) {
                    st.execute("ALTER TABLE " + e.getKey() + " AUTO_INCREMENT = " + e.getValue());
                }
            }
        }
    }

    // ---------------------------------------------------------------- ayudas

    static String json(ResultActions r) throws Exception {
        return r.andReturn().getResponse().getContentAsString();
    }

    static int estado(ResultActions r) {
        return r.andReturn().getResponse().getStatus();
    }

    ResultActions postJson(String cuerpo) throws Exception {
        return http.perform(post("/bancaria/").contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    ResultActions putJson(long id, String cuerpo) throws Exception {
        return http.perform(put("/bancaria/" + id).contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    /** Alta por la API con datos válidos; devuelve el id. */
    long altaPorApi(String extra) throws Exception {
        var alta = postJson("{\"banco\":\"" + MARCA + "\",\"numero\":\"409-001\",\"titular\":\"Titular E2E\","
                + "\"cuit\":\"30518594466\",\"numeroCuenta\":" + cuentaReal + (extra.isEmpty() ? "" : "," + extra) + "}");
        assertThat(estado(alta)).as(json(alta)).isEqualTo(200);
        return ((Number) JsonPath.read(json(alta), "$.bancariaId")).longValue();
    }

    Bancaria altaDeCasoDeUso() {
        return crear.createBancaria(BancariaCambios.builder().banco(Campo.de(MARCA)).numero(Campo.de("409-001"))
                .titular(Campo.de("Titular E2E")).numeroCuenta(Campo.de(new java.math.BigDecimal(cuentaReal))).build());
    }

    static long historiales(long id) throws SQLException {
        return contar("SELECT COUNT(*) FROM gestion_escritura_historial WHERE entidad = 'bancaria' AND entidad_clave = '" + id + "'");
    }

    static String operaciones(long id) throws SQLException {
        return texto("SELECT GROUP_CONCAT(operacion ORDER BY escritura_historial_id) FROM gestion_escritura_historial"
                + " WHERE entidad = 'bancaria' AND entidad_clave = '" + id + "'");
    }

    static String fila(long id) throws SQLException {
        return texto("SELECT CONCAT_WS('|', CuB_Banco, CuB_Numero, CuB_CBU, CuB_Titular, CuB_CUIT, CuB_Pla_Cuenta, IFNULL(CuB_Prv_ID, 'NULL'))"
                + " FROM bancaria WHERE CuB_ID = " + id);
    }

    /** Valor marcado de la cuenta, solo para ocupar la FK de la baja. */
    static void valorMarcado(long bancariaId) throws SQLException {
        ejecutar("INSERT INTO valores (Val_CuentaOrigen, Val_Estado, Val_NombreTitular, created) VALUES (" + bancariaId
                + ", 'Creado', '" + MARCA + "', NOW())");
    }

    static void movimientoMarcado(long bancariaId) throws SQLException {
        ejecutar("INSERT INTO libro_banco (LiB_CuB_ID, LiB_Fecha, LiB_Orden, LiB_Tipo, LiB_Concepto, created) VALUES (" + bancariaId
                + ", '2099-01-01', 409, 'Cheque', '" + MARCA + "', NOW())");
    }

    List<Map<String, Object>> listado() throws Exception {
        var respuesta = http.perform(get("/bancaria/"));
        assertThat(estado(respuesta)).isEqualTo(200);
        return JsonPath.read(json(respuesta), "$");
    }

    @SuppressWarnings("unchecked")
    static Map<Integer, Map<String, Object>> porId(List<Map<String, Object>> filas) {
        Map<Integer, Map<String, Object>> r = new LinkedHashMap<>();
        for (var f : filas) r.put((Integer) f.get("bancariaId"), f);
        return r;
    }

    // ---------------------------------------------------------------- alta, edición y baja por la API

    @Test
    void altaEdicionYBajaPorLaApiContraMysqlConHistorialYSinTocarLasAjenas() throws Exception {
        var antes = porId(listado());

        long id = altaPorApi("\"cbu\":\"\",\"proveedorId\":" + proveedorReal);
        assertThat(fila(id)).isEqualTo(MARCA + "|409-001||Titular E2E|30-51859446-6|" + cuentaReal + "|" + proveedorReal);
        assertThat(texto("SELECT uuid FROM bancaria WHERE CuB_ID = " + id)).matches("[0-9a-f]{32}");
        assertThat(texto("SELECT created FROM bancaria WHERE CuB_ID = " + id)).isNotNull();
        assertThat(texto("SELECT updated FROM bancaria WHERE CuB_ID = " + id)).isNotNull();
        assertThat(operaciones(id)).isEqualTo("ALTA");

        // El listado legacy ve la fila propia con sus claves y deja igual a las ajenas.
        var despuesDelAlta = porId(listado());
        assertThat(despuesDelAlta.get((int) id)).containsOnlyKeys("bancariaId", "banco", "numero", "cbu", "titular", "cuit",
                "numeroCuenta", "proveedorId", "cuenta", "created", "updated");
        assertThat(despuesDelAlta.get((int) id).get("banco")).isEqualTo(MARCA);
        assertThat(((Map<?, ?>) despuesDelAlta.get((int) id).get("cuenta")).get("numeroCuenta")).isNotNull();
        antes.forEach((k, v) -> assertThat(despuesDelAlta.get(k)).as("cuenta ajena " + k).isEqualTo(v));

        // GET por id: mismas claves planas del listado, sin la cuenta anidada.
        var uno = http.perform(get("/bancaria/" + id));
        assertThat(estado(uno)).isEqualTo(200);
        Map<String, Object> plano = JsonPath.read(json(uno), "$");
        assertThat(plano).containsOnlyKeys("bancariaId", "banco", "numero", "cbu", "titular", "cuit", "numeroCuenta", "proveedorId", "created", "updated");
        plano.forEach((k, v) -> assertThat(despuesDelAlta.get((int) id).get(k)).as(k).isEqualTo(v));

        // Edición por presencia: titular nuevo, proveedor a null; lo ausente se conserva.
        var edicion = putJson(id, "{\"titular\":\"Otro titular\",\"proveedorId\":null}");
        assertThat(estado(edicion)).as(json(edicion)).isEqualTo(200);
        assertThat(fila(id)).isEqualTo(MARCA + "|409-001||Otro titular|30-51859446-6|" + cuentaReal + "|NULL");
        assertThat(operaciones(id)).isEqualTo("ALTA,EDICION");

        // Sin cambios efectivos: 200 y sin evento. Un payload completo como el de VB6 tampoco cambia nada.
        assertThat(estado(putJson(id, "{}"))).isEqualTo(200);
        assertThat(estado(putJson(id, "{\"bancariaId\":" + id + ",\"banco\":\"" + MARCA + "\",\"numero\":\"409-001\",\"cbu\":\"\","
                + "\"titular\":\"Otro titular\",\"cuit\":\"30-51859446-6\",\"numeroCuenta\":" + cuentaReal + ",\"proveedorId\":null}"))).isEqualTo(200);
        assertThat(operaciones(id)).isEqualTo("ALTA,EDICION");

        var despuesDeEditar = porId(listado());
        antes.forEach((k, v) -> assertThat(despuesDeEditar.get(k)).as("cuenta ajena " + k).isEqualTo(v));

        var baja = http.perform(delete("/bancaria/" + id));
        assertThat(estado(baja)).isEqualTo(204);
        assertThat(json(baja)).isEmpty();
        assertThat(contar("SELECT COUNT(*) FROM bancaria WHERE CuB_ID = " + id)).isZero();
        assertThat(operaciones(id)).isEqualTo("ALTA,EDICION,BAJA");
        // La baja guarda el último estado y el historial no inventa actor.
        assertThat(texto("SELECT valor_anterior FROM gestion_escritura_historial WHERE entidad = 'bancaria' AND entidad_clave = '" + id
                + "' AND operacion = 'BAJA'")).contains("Otro titular").doesNotContain("actor");
        assertThat(porId(listado())).isEqualTo(antes);
    }

    @Test
    void validacionesDeLaApiConErroresConClaveStatusYSinEscribirNada() throws Exception {
        long cuentas = contar("SELECT COUNT(*) FROM bancaria");
        String base = "\"banco\":\"" + MARCA + "\",\"numeroCuenta\":" + cuentaReal;
        Map<String, String> casos = new LinkedHashMap<>();
        casos.put("{" + base + ",\"bancariaId\":5}", "ID_NO_PERMITIDO");
        casos.put("{" + base + ",\"cuit\":\"30-51859446-5\"}", "CUIT_INVALIDO");
        casos.put("{" + base + ",\"cbu\":\"0070999000001234567892\"}", "CBU_INVALIDO");
        casos.put("{" + base + ",\"titular\":\"" + "t".repeat(65) + "\"}", "TEXTO_INVALIDO");
        casos.put("{" + base + ",\"titular\":\"Banco 🏦\"}", "TEXTO_INVALIDO");
        casos.put("{" + base + ",\"proveedorId\":12.7}", "NUMERO_INVALIDO");
        casos.put("{" + base + ",\"proveedorId\":40000}", "NUMERO_INVALIDO");
        casos.put("{" + base + ",\"proveedorId\":32767}", "PROVEEDOR_INEXISTENTE");
        casos.put("{\"banco\":\"" + MARCA + "\"}", "CUENTA_REQUERIDA");
        casos.put("{\"banco\":\"" + MARCA + "\",\"numeroCuenta\":99999999999}", "CUENTA_INEXISTENTE");
        casos.put("{no es json", "SOLICITUD_INVALIDA");
        for (var caso : casos.entrySet()) {
            var respuesta = postJson(caso.getKey());
            assertThat(estado(respuesta)).as(caso.getKey()).isEqualTo(400);
            assertThat((String) JsonPath.read(json(respuesta), "$.codigo")).as(caso.getKey()).isEqualTo(caso.getValue());
            assertThat((Integer) JsonPath.read(json(respuesta), "$.status")).isEqualTo(400);
        }
        assertThat(contar("SELECT COUNT(*) FROM bancaria")).isEqualTo(cuentas);

        // PUT: id del cuerpo distinto, cuenta contable vacía y 404 con cuerpo.
        long id = altaPorApi("");
        assertThat((String) JsonPath.read(json(putJson(id, "{\"bancariaId\":" + (id + 1) + "}")), "$.codigo")).isEqualTo("ID_NO_COINCIDE");
        assertThat((String) JsonPath.read(json(putJson(id, "{\"numeroCuenta\":null}")), "$.codigo")).isEqualTo("CUENTA_REQUERIDA");
        assertThat(fila(id)).isEqualTo(MARCA + "|409-001||Titular E2E|30-51859446-6|" + cuentaReal + "|NULL");
        for (var inexistente : List.of(http.perform(get("/bancaria/999999")), putJson(999999, "{}"), http.perform(delete("/bancaria/999999")))) {
            assertThat(estado(inexistente)).isEqualTo(404);
            assertThat((Integer) JsonPath.read(json(inexistente), "$.status")).isEqualTo(404);
        }
        assertThat(estado(http.perform(get("/bancaria/999999/uso")))).isEqualTo(404);
        assertThat(operaciones(id)).isEqualTo("ALTA");
    }

    @Test
    void elTextoLatin1ViajaEntreLaApiYLaColumnaYMysqlRechazaLoQueLaApiFrena() throws Exception {
        long id = altaPorApi("");
        assertThat(estado(putJson(id, "{\"titular\":\"Ñandú Peña € “ok”\"}"))).isEqualTo(200);
        assertThat(texto("SELECT CuB_Titular FROM bancaria WHERE CuB_ID = " + id)).isEqualTo("Ñandú Peña € “ok”");
        assertThat(texto("SELECT HEX(CuB_Titular) FROM bancaria WHERE CuB_ID = " + id)).startsWith("D1").contains("80");
        assertThat(JsonPath.<String>read(json(http.perform(get("/bancaria/" + id))), "$.titular")).isEqualTo("Ñandú Peña € “ok”");
        // Prueba del motivo de TEXTO_INVALIDO: sin la validación, MySQL en modo estricto rechaza el emoji (1366).
        assertThatThrownBy(() -> ejecutar("UPDATE bancaria SET CuB_Titular = '🏦' WHERE CuB_ID = " + id))
                .hasMessageContaining("Incorrect string value");
        assertThat(texto("SELECT CuB_Titular FROM bancaria WHERE CuB_ID = " + id)).isEqualTo("Ñandú Peña € “ok”");
    }

    @Test
    void unCuitHistoricoInvalidoQueNoSeTocaNoSeRevalidaYElCbuValidoSeGuarda() throws Exception {
        long id = altaPorApi("");
        ejecutar("UPDATE bancaria SET CuB_CUIT = '20-12345678-0', CuB_CBU = '123' WHERE CuB_ID = " + id);
        var edicion = putJson(id, "{\"titular\":\"Solo titular\"}");
        assertThat(estado(edicion)).as(json(edicion)).isEqualTo(200);
        assertThat(fila(id)).isEqualTo(MARCA + "|409-001|123|Solo titular|20-12345678-0|" + cuentaReal + "|NULL");
        assertThat(estado(putJson(id, "{\"cuit\":\"20-12345678-0\",\"cbu\":\"123\"}"))).isEqualTo(200);
        assertThat((String) JsonPath.read(json(putJson(id, "{\"cuit\":\"20-12345678-1\"}")), "$.codigo")).isEqualTo("CUIT_INVALIDO");
        assertThat(estado(putJson(id, "{\"cbu\":\"0070999000001234567891\"}"))).isEqualTo(200);
        assertThat(texto("SELECT CuB_CBU FROM bancaria WHERE CuB_ID = " + id)).isEqualTo("0070999000001234567891");
        assertThat(estado(putJson(id, "{\"cbu\":null,\"cuit\":null}"))).isEqualTo(200);
        assertThat(fila(id)).isEqualTo(MARCA + "|409-001||Solo titular||" + cuentaReal + "|NULL");
    }

    // ---------------------------------------------------------------- uso, baja bloqueada y FK real

    @Test
    void elUsoYLaBajaDeUnaCuentaConReferenciasRealesDanEnUsoSinBorradoParcial() throws Exception {
        // La cuenta con más movimientos de libro banco de dev (hoy la 8, Santander): solo lectura y una baja rechazada.
        long id = contar("SELECT LiB_CuB_ID FROM libro_banco GROUP BY LiB_CuB_ID ORDER BY COUNT(*) DESC LIMIT 1");
        long libro = contar("SELECT COUNT(*) FROM libro_banco WHERE LiB_CuB_ID = " + id);
        long valores = contar("SELECT COUNT(*) FROM valores WHERE Val_CuentaOrigen = " + id);
        String antes = fila(id);
        long historialAntes = contar("SELECT COUNT(*) FROM gestion_escritura_historial");

        var usoJson = json(http.perform(get("/bancaria/" + id + "/uso")));
        assertThat(JsonPath.<Number>read(usoJson, "$.libroBanco").longValue()).isEqualTo(libro);
        assertThat(JsonPath.<Number>read(usoJson, "$.valores").longValue()).isEqualTo(valores);
        assertThat(JsonPath.<Boolean>read(usoJson, "$.eliminable")).isFalse();

        var baja = http.perform(delete("/bancaria/" + id));
        assertThat(estado(baja)).isEqualTo(409);
        assertThat((String) JsonPath.read(json(baja), "$.codigo")).isEqualTo("EN_USO");
        assertThat((String) JsonPath.read(json(baja), "$.message")).contains(libro + " movimientos");
        assertThat(fila(id)).as("la cuenta sigue intacta").isEqualTo(antes);
        assertThat(contar("SELECT COUNT(*) FROM libro_banco WHERE LiB_CuB_ID = " + id)).isEqualTo(libro);
        assertThat(contar("SELECT COUNT(*) FROM gestion_escritura_historial")).as("sin evento de baja").isEqualTo(historialAntes);

        // Una cuenta libre real: eliminable. (Solo se consulta: no se borra una cuenta ajena.)
        long libre = contar("SELECT MIN(CuB_ID) FROM bancaria b WHERE NOT EXISTS (SELECT 1 FROM libro_banco l WHERE l.LiB_CuB_ID = b.CuB_ID)"
                + " AND NOT EXISTS (SELECT 1 FROM valores v WHERE v.Val_CuentaOrigen = b.CuB_ID) AND CuB_Banco NOT LIKE '" + PREFIJO + "%'");
        assertThat(JsonPath.<Boolean>read(json(http.perform(get("/bancaria/" + libre + "/uso"))), "$.eliminable")).isTrue();
    }

    @Test
    void bajaDeCuentaPropiaConValoresOMovimientosEsEnUsoYSeBorraCuandoSeLibera() throws Exception {
        long id = altaPorApi("");
        valorMarcado(id);
        movimientoMarcado(id);
        var uso = json(http.perform(get("/bancaria/" + id + "/uso")));
        assertThat(JsonPath.<Number>read(uso, "$.valores").longValue()).isEqualTo(1);
        assertThat(JsonPath.<Number>read(uso, "$.libroBanco").longValue()).isEqualTo(1);
        var baja = http.perform(delete("/bancaria/" + id));
        assertThat(estado(baja)).isEqualTo(409);
        assertThat((String) JsonPath.read(json(baja), "$.message")).contains("1 valores").contains("1 movimientos");
        assertThat(operaciones(id)).isEqualTo("ALTA");
        ejecutar("DELETE FROM libro_banco WHERE LiB_Concepto LIKE '" + PREFIJO + "%'");
        assertThat(estado(http.perform(delete("/bancaria/" + id)))).as("quedan valores").isEqualTo(409);
        ejecutar("DELETE FROM valores WHERE Val_NombreTitular LIKE '" + PREFIJO + "%'");
        assertThat(estado(http.perform(delete("/bancaria/" + id)))).isEqualTo(204);
        assertThat(operaciones(id)).isEqualTo("ALTA,BAJA");
    }

    @Test
    void laFkRealDeMysqlSeClasificaComoForeignKeyEnLaBajaYEnElAlta() throws Exception {
        // Baja: la referencia aparece después del chequeo (se ejecuta el adaptador, sin pasar por existsBy).
        Bancaria propia = altaDeCasoDeUso();
        valorMarcado(propia.getBancariaId());
        assertThatThrownBy(() -> transaction.execute(status -> {
            repositorio.deleteByBancariaId(propia.getBancariaId());
            return null;
        })).isInstanceOfSatisfying(BancariaConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.EN_USO));
        assertThat(contar("SELECT COUNT(*) FROM bancaria WHERE CuB_ID = " + propia.getBancariaId())).isEqualTo(1);

        // Alta y edición: la cuenta contable y el proveedor desaparecen después de validarlos (se omite la validación).
        Bancaria inexistente = propia.toBuilder().bancariaId(null).numeroCuenta(new java.math.BigDecimal("99999999999")).uuid(UUID.randomUUID().toString().replace("-", "")).build();
        assertThatThrownBy(() -> transaction.execute(status -> repositorio.create(inexistente)))
                .isInstanceOfSatisfying(BancariaConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CONFLICTO));
        assertThatThrownBy(() -> transaction.execute(status -> repositorio.update(propia.toBuilder().proveedorId(32767).build())))
                .isInstanceOfSatisfying(BancariaConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CONFLICTO));
        assertThat(fila(propia.getBancariaId())).endsWith("|NULL");
        // Y lo que no es una FK no se disfraza: un NOT NULL (titular null) sigue siendo un error de datos.
        assertThatThrownBy(() -> transaction.execute(status -> repositorio.update(propia.toBuilder().titular(null).build())))
                .isNotInstanceOf(BancariaConflictoException.class);
    }

    // ---------------------------------------------------------------- precondición updated

    @Test
    void laPrecondicionUpdatedDetectaElCambioConcurrenteConResolucionDeUnSegundo() throws Exception {
        long id = altaPorApi("");
        String leido = JsonPath.read(json(http.perform(get("/bancaria/" + id))), "$.updated");
        Thread.sleep(1_100); // updated tiene resolución de un segundo
        assertThat(estado(putJson(id, "{\"titular\":\"Primero\"}"))).isEqualTo(200);
        String nuevo = JsonPath.read(json(http.perform(get("/bancaria/" + id))), "$.updated");
        assertThat(nuevo).isNotEqualTo(leido);

        var viejo = putJson(id, "{\"updated\":\"" + leido + "\",\"titular\":\"Pisa al primero\"}");
        assertThat(estado(viejo)).isEqualTo(409);
        assertThat((String) JsonPath.read(json(viejo), "$.codigo")).isEqualTo("CAMBIO_CONCURRENTE");
        assertThat(texto("SELECT CuB_Titular FROM bancaria WHERE CuB_ID = " + id)).isEqualTo("Primero");
        assertThat(operaciones(id)).isEqualTo("ALTA,EDICION");

        assertThat(estado(putJson(id, "{\"updated\":\"" + nuevo + "\",\"titular\":\"Segundo\"}"))).isEqualTo(200);
        assertThat(texto("SELECT CuB_Titular FROM bancaria WHERE CuB_ID = " + id)).isEqualTo("Segundo");
    }

    // ---------------------------------------------------------------- concurrencia

    static void pausarAntesDe(String prefijo) {
        enPausa = new CountDownLatch(1);
        reanudar = new CountDownLatch(1);
        pausarEn = sql -> sql.startsWith(prefijo);
    }

    /** Sentencia de la transacción que espera un bloqueo de fila (INNODB_LOCK_WAITS), o null si ninguna esperó. */
    static String esperaEn(Future<?> tarea, long segundos) throws Exception {
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(segundos);
        while (System.nanoTime() < limite && !tarea.isDone()) {
            String consulta = texto("SELECT LOWER(r.trx_query) FROM information_schema.INNODB_LOCK_WAITS w"
                    + " JOIN information_schema.INNODB_TRX r ON r.trx_id = w.requesting_trx_id LIMIT 1");
            if (consulta != null) return consulta;
            Thread.sleep(100);
        }
        return null;
    }

    record Cruce(String esperaDeB, Throwable errorA, Throwable errorB) { }

    /** A queda pausada antes de escribir; B arranca; se mide si B espera un bloqueo; se libera A y se recogen los resultados. */
    Cruce cruzar(String pausaDeA, Callable<?> a, Callable<?> b, long segundosDeEspera) throws Exception {
        try (ExecutorService hiloA = Executors.newSingleThreadExecutor(r -> new Thread(r, HILO));
             ExecutorService hiloB = Executors.newSingleThreadExecutor()) {
            pausarAntesDe(pausaDeA);
            Future<?> fa = hiloA.submit(a);
            assertThat(enPausa.await(60, TimeUnit.SECONDS)).as("A llegó a la pausa").isTrue();
            Future<?> fb = hiloB.submit(b);
            String espera = esperaEn(fb, segundosDeEspera);
            reanudar.countDown();
            return new Cruce(espera, error(fa), error(fb));
        }
    }

    static Throwable error(Future<?> f) throws Exception {
        try {
            f.get(5, TimeUnit.MINUTES);
            return null;
        } catch (ExecutionException ex) {
            return ex.getCause();
        }
    }

    /** Mutante: lee sin bloqueo la cuenta; el resto delega en el adaptador real. */
    BancariaRepository mutante() {
        return new BancariaRepository() {
            @Override
            public java.util.Optional<Bancaria> findByBancariaId(Long id) {
                return repositorio.findByBancariaId(id);
            }

            @Override
            public java.util.Optional<Bancaria> findWithLockByBancariaId(Long id) {
                return java.util.Optional.ofNullable(entityManager.find(BancariaEntity.class, id)).map(mapper::toDomain);
            }

            @Override
            public Bancaria create(Bancaria b) { return repositorio.create(b); }

            @Override
            public Bancaria update(Bancaria b) { return repositorio.update(b); }

            @Override
            public void deleteByBancariaId(Long id) { repositorio.deleteByBancariaId(id); }
        };
    }

    @Test
    void dosPutCruzadosSobreLaMismaCuentaPersistenAmbosCambios() throws Exception {
        long id = altaPorApi("");
        var cruce = cruzar("update bancaria",
                () -> editar.updateBancaria(id, BancariaCambios.builder().titular(Campo.de("Titular A")).build()),
                () -> editar.updateBancaria(id, BancariaCambios.builder().cbu(Campo.de("0070999000001234567891")).build()), 30);
        assertThat(cruce.esperaDeB()).as("B espera el bloqueo de la fila").contains("for update");
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
        assertThat(fila(id)).isEqualTo(MARCA + "|409-001|0070999000001234567891|Titular A|30-51859446-6|" + cuentaReal + "|NULL");
        assertThat(operaciones(id)).isEqualTo("ALTA,EDICION,EDICION");
        // El segundo evento parte del estado que dejó el primero: el historial no queda con un «antes» viejo.
        assertThat(texto("SELECT valor_anterior FROM gestion_escritura_historial WHERE entidad = 'bancaria' AND entidad_clave = '" + id
                + "' AND operacion = 'EDICION' ORDER BY escritura_historial_id DESC LIMIT 1")).contains("Titular A");
    }

    @Test
    void mutanteSinLockLeeLaFilaAntesDeQueTermineElOtroPutYNoEspera() throws Exception {
        long id = altaPorApi("");
        var sinLock = new UpdateBancariaUseCaseImpl(mutante(), referencias, historial);
        var cruce = cruzar("update bancaria",
                () -> editar.updateBancaria(id, BancariaCambios.builder().titular(Campo.de("Titular A")).build()),
                () -> transaction.execute(status -> sinLock.updateBancaria(id,
                        BancariaCambios.builder().cbu(Campo.de("0070999000001234567891")).build())), 5);
        // Sin el lock B no espera a A: parte de lo mismo que leyó A. (La entidad usa @DynamicUpdate, así que el valor
        // final de columnas distintas coincide; lo que distingue al mutante es la falta de espera.)
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
        assertThat(cruce.esperaDeB()).as("sin lock B no espera por la lectura, solo por el UPDATE").doesNotContain("for update");
    }

    @Test
    void putContraDeleteLaBajaRegistraElUltimoEstadoConfirmado() throws Exception {
        long id = altaPorApi("");
        var cruce = cruzar("update bancaria",
                () -> editar.updateBancaria(id, BancariaCambios.builder().titular(Campo.de("Titular editado")).build()),
                () -> { borrar.deleteBancaria(id); return null; }, 30);
        assertThat(cruce.esperaDeB()).as("la baja espera el bloqueo de la fila").contains("for update");
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
        assertThat(contar("SELECT COUNT(*) FROM bancaria WHERE CuB_ID = " + id)).isZero();
        assertThat(operaciones(id)).isEqualTo("ALTA,EDICION,BAJA");
        assertThat(texto("SELECT valor_anterior FROM gestion_escritura_historial WHERE entidad = 'bancaria' AND entidad_clave = '" + id
                + "' AND operacion = 'BAJA'")).as("la baja vio la edición confirmada").contains("Titular editado");
    }

    @Test
    void mutanteSinLockEnLaBajaRegistraUnEstadoPreviosViejo() throws Exception {
        long id = altaPorApi("");
        var bajaSinLock = new DeleteBancariaUseCaseImpl(mutante(), usoAdapter, historial);
        var cruce = cruzar("update bancaria",
                () -> editar.updateBancaria(id, BancariaCambios.builder().titular(Campo.de("Titular editado")).build()),
                () -> transaction.execute(status -> { bajaSinLock.deleteBancaria(id); return null; }), 30);
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).as("error de la baja sin lock").isNull();
        // La lectura sin lock no espera; la baja se bloquea recién en el DELETE, con el estado ya viejo en la mano.
        assertThat(cruce.esperaDeB()).as("espera en el delete, no en la lectura").startsWith("delete");
        assertThat(texto("SELECT valor_anterior FROM gestion_escritura_historial WHERE entidad = 'bancaria' AND entidad_clave = '" + id
                + "' AND operacion = 'BAJA'")).as("el mutante registra el titular previo a la edición").contains("Titular E2E");
    }
}
