package um.tesoreria.core.e2e;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
import jakarta.persistence.EntityManager;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.controller.EjercicioController;
import um.tesoreria.core.exception.EjercicioBloqueadoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.application.usecases.ExistsAsientoByFechaBetweenUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.adapter.JpaAsientoRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository.JpaAsientoRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases.ExistsByFechaContableBetweenUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.adapter.JpaCuentaMovimientoRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.mapper.CuentaMovimientoMapper;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases.CreateEjercicioUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases.DeleteEjercicioUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases.UpdateEjercicioUseCaseImpl;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter.EjercicioHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter.EjercicioUsoAdapter;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter.JpaEjercicioRepositoryAdapter;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.entity.EjercicioEntity;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.mapper.EjercicioMapper;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.mapper.EjercicioDtoMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;
import um.tesoreria.core.service.EjercicioService;
import um.tesoreria.core.service.facade.ContabilidadService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Alta, baja y bloqueo de ejercicios (#408) contra MySQL de desarrollo. Opt-in: escribe en tablas reales.
 * Ejecutar a mano, siempre en local: E2E_DEV_ESCRITURA=si, IT_DB_* y -Dit.test=EjercicioDevE2E.
 * <p>
 * Qué es real: MySQL 5.7 de dev (FK RESTRICT de Compras, locks, historial), los casos de uso, los adaptadores JPA y el
 * controlador legacy (MockMvc standalone, sin Tomcat). Qué es simulado: el reloj ({@code Clock} fijo en 2026-10-21) y los
 * colaboradores de ContabilidadService que no leen el flag (mocks). No se prueba con el ejecutable VB6.
 * <p>
 * Escribe: ejercicios 16 y 17, filas marcadas en compra_referencia (ejercicio_id 16/17, importe 408.08), movcon y asiento
 * ({@code E2E-408-*}) y eventos de historial de esos ejercicios. Aborta sin tocar nada si el ejercicio 16 ya existe.
 * Al terminar borra lo propio, restaura los AUTO_INCREMENT e informa (no falla) si el CHECKSUM difiere: dev es compartida.
 * Las concurrencias fuerzan el cruce con una pausa del SQL y comprueban el LOCK WAIT en INNODB_TRX; cada una se repite
 * con un mutante (lectura sin bloqueo) que tiene que perder la carrera.
 */
@DataJpaTest(properties = {"spring.jpa.show-sql=false"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "E2E_DEV_ESCRITURA", matches = "si")
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateEjercicioUseCaseImpl.class, UpdateEjercicioUseCaseImpl.class, DeleteEjercicioUseCaseImpl.class,
        JpaEjercicioRepositoryAdapter.class, EjercicioMapper.class, EjercicioHistorialAdapter.class,
        EjercicioUsoAdapter.class, ExistsByFechaContableBetweenUseCaseImpl.class, JpaCuentaMovimientoRepositoryAdapter.class,
        CuentaMovimientoMapper.class, ExistsAsientoByFechaBetweenUseCaseImpl.class, JpaAsientoRepositoryAdapter.class,
        EjercicioDtoMapper.class, EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class, EjercicioDevE2E.Config.class})
class EjercicioDevE2E {
    static final String PREFIJO = "E2E-408-";
    static final String MARCA = PREFIJO + UUID.randomUUID().toString().substring(0, 8);
    static final String HILO = "ejercicio408";
    static final int ORDEN_RESERVADO = 9408; // smallint
    static final String IMPORTE_MARCADO = "408.08";
    static final List<String> TABLAS = List.of("ejercicios", "gestion_escritura_historial", "asiento", "movcon", "compra_referencia");
    static final List<String> CON_AUTO_INCREMENT = List.of("gestion_escritura_historial", "asiento", "movcon");
    static final List<String> ESCRIBIBLES = List.of("ejercicios", "gestion_escritura_historial", "asiento");

    /** Verdadero solo si @BeforeAll comprobó que el ejercicio 16 no existía: sin eso, nada de lo que hay en dev es propio. */
    static volatile boolean propietario;
    static Map<String, Long> fotoInicial;
    static Map<String, Long> autoIncrementInicial;
    static volatile Predicate<String> pausarEn;
    static volatile CountDownLatch enPausa;
    static volatile CountDownLatch reanudar;

    @TestConfiguration
    @EnableJpaAuditing
    static class Config {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-10-21T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        }
    }

    @Autowired CreateEjercicioUseCaseImpl crear;
    @Autowired UpdateEjercicioUseCaseImpl editar;
    @Autowired DeleteEjercicioUseCaseImpl borrar;
    @Autowired EjercicioRepository repositorio;
    @Autowired EjercicioMapper mapper;
    @Autowired EjercicioDtoMapper dtoMapper;
    @Autowired EjercicioHistorialAdapter historial;
    @Autowired EjercicioUsoAdapter uso;
    @Autowired TransactionOperations transaction;
    @Autowired EntityManager entityManager;
    @Autowired um.tesoreria.core.repository.EjercicioRepository repositorioLegacy;
    @Autowired JpaAsientoRepository asientosJpa;
    @Autowired Clock clock;

    MockMvc http;

    static String url() {
        return "jdbc:mysql://%s:%s/%s?sslMode=REQUIRED&serverTimezone=UTC&connectTimeout=60000&socketTimeout=300000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium"));
    }

    @DynamicPropertySource
    static void dev(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", EjercicioDevE2E::url);
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
                throw new IllegalStateException("Escritura fuera del flujo de ejercicios: " + escritura.group(1));
            }
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
            // Aborta sin escribir: otra sesión ya usa el ejercicio que esta prueba va a crear.
            assertThat(contar(db, "SELECT COUNT(*) FROM ejercicios WHERE Eje_ID >= 16"))
                    .as("el ejercicio 16 ya existe: otra sesión escribió en dev; la prueba aborta sin tocar nada").isZero();
            propietario = true;
            limpiarPropio(db); // residuos marcados de una corrida interrumpida
            fotoInicial = checksums(db);
            autoIncrementInicial = autoIncrements(db);
        }
    }

    @AfterAll
    static void restaurar() throws Exception {
        if (!propietario) {
            return; // la prueba abortó antes de escribir: no se borra ni se restaura nada
        }
        try (var db = abrir()) {
            try {
                limpiarPropio(db);
            } finally {
                restaurarAutoIncrements(db);
            }
            var autoIncrementFinal = autoIncrements(db);
            var fotoFinal = checksums(db);
            System.out.println("E2E-408 AUTO_INCREMENT como antes: " + autoIncrementFinal.equals(autoIncrementInicial));
            System.out.println("E2E-408 tablas idénticas a antes: " + fotoFinal.equals(fotoInicial)
                    + (fotoFinal.equals(fotoInicial) ? "" : " (difieren: " + fotoInicial + " -> " + fotoFinal + "; ver si otra sesión escribió en dev)"));
            assertThat(contar(db, "SELECT COUNT(*) FROM ejercicios WHERE Eje_ID >= 16")).as("sin ejercicios propios").isZero();
        }
    }

    @BeforeEach
    void preparar() throws Exception {
        assertThat(propietario).as("sin propiedad verificada no se escribe en dev").isTrue();
        try (var db = abrir()) {
            limpiarPropio(db);
            assertThat(contar(db, "SELECT MAX(Eje_ID) FROM ejercicios")).as("el último ejercicio de dev tiene que ser el 15").isEqualTo(15);
        }
        http = MockMvcBuilders.standaloneSetup(new EjercicioController(new EjercicioService(repositorioLegacy),
                crear, editar, borrar, dtoMapper))
                .setControllerAdvice(new um.tesoreria.core.controller.EjercicioExceptionAdvice()).build();
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

    static void limpiarPropio(Connection db) throws SQLException {
        for (String sql : List.of(
                "DELETE FROM compra_referencia WHERE ejercicio_id IN (16, 17) AND importe = " + IMPORTE_MARCADO,
                "DELETE FROM movcon WHERE MCo_Concepto LIKE '" + PREFIJO + "%'",
                "DELETE FROM asiento WHERE vinculo LIKE '" + PREFIJO + "%'",
                "DELETE FROM gestion_escritura_historial WHERE entidad = 'ejercicio' AND entidad_clave IN ('16', '17')",
                "DELETE FROM ejercicios WHERE Eje_ID IN (16, 17)")) {
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

    static String texto(String sql) throws SQLException {
        try (var db = abrir(); var st = db.createStatement(); var rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : null;
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

    static Map<String, Long> autoIncrements(Connection db) throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        for (String tabla : CON_AUTO_INCREMENT) {
            r.put(tabla, contar(db, "SELECT auto_increment FROM information_schema.TABLES WHERE table_schema = DATABASE()"
                    + " AND table_name = '" + tabla + "'"));
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

    Ejercicio alta() {
        return crear.createEjercicio(Ejercicio.builder().build());
    }

    static void rechaza(Codigo codigo, Callable<?> accion) {
        assertThatThrownBy(accion::call).isInstanceOfSatisfying(EjercicioConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(codigo));
    }

    static long historiales(int ejercicioId) throws SQLException {
        return contar("SELECT COUNT(*) FROM gestion_escritura_historial WHERE entidad = 'ejercicio' AND entidad_clave = '" + ejercicioId + "'");
    }

    static void movimientoMarcado(String fecha) throws SQLException {
        ejecutar("INSERT INTO movcon (MCo_Fecha, MCo_NroComp, MCo_Item, MCo_Pla_Cuenta, MCo_Concepto, created) VALUES ('"
                + fecha + " 00:00:00', " + ORDEN_RESERVADO + ", 1, 10000000000, '" + MARCA + "', NOW())");
    }

    static void asientoMarcado(String fecha) throws SQLException {
        ejecutar("INSERT INTO asiento (fecha, orden, vinculo, uuid, created) VALUES ('" + fecha + " 00:00:00', "
                + ORDEN_RESERVADO + ", '" + MARCA + "', '" + UUID.randomUUID().toString().replace("-", "") + "', NOW())");
    }

    String json(org.springframework.test.web.servlet.ResultActions r) throws Exception {
        return r.andReturn().getResponse().getContentAsString();
    }

    // ---------------------------------------------------------------- alta

    @Test
    void altaDelSiguienteBisiestoYLimiteConRelojDe2026() throws Exception {
        Ejercicio e16 = alta();
        assertThat(e16.getEjercicioId()).isEqualTo(16);
        assertThat(e16.getFechaInicio()).isEqualTo(LocalDate.parse("2027-03-01"));
        assertThat(e16.getFechaFinal()).isEqualTo(LocalDate.parse("2028-02-29")); // caso bisiesto de la issue
        assertThat(texto("SELECT Eje_Nombre FROM ejercicios WHERE Eje_ID = 16")).isEqualTo("Ejercicio Económico 2027");
        assertThat(texto("SELECT Eje_FechaFin FROM ejercicios WHERE Eje_ID = 16")).startsWith("2028-02-29 00:00:00");
        assertThat(historiales(16)).isEqualTo(1);
        assertThat(alta().getEjercicioId()).isEqualTo(17);
        rechaza(Codigo.LIMITE_ALTA, this::alta);
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 18")).isZero();
    }

    @Test
    void saltoYDuplicadoSeRechazanSinEscribir() throws Exception {
        rechaza(Codigo.SALTO, () -> crear.createEjercicio(Ejercicio.builder().ejercicioId(17).build()));
        rechaza(Codigo.DUPLICADO, () -> crear.createEjercicio(Ejercicio.builder().ejercicioId(15).build()));
        alta();
        rechaza(Codigo.DUPLICADO, () -> crear.createEjercicio(Ejercicio.builder().ejercicioId(16).build()));
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID >= 16")).isEqualTo(1);
        assertThat(historiales(17)).isZero();
        // Contrato HTTP: 409 con JSON y código, no un 500 vacío que VB6 leería como éxito.
        var salto = http.perform(post("/ejercicio/").contentType(MediaType.APPLICATION_JSON).content("{\"ejercicioId\":19}"));
        assertThat(salto.andReturn().getResponse().getStatus()).isEqualTo(409);
        assertThat(json(salto)).contains("\"codigo\":\"SALTO\"");
    }

    // ---------------------------------------------------------------- baja

    @Test
    void bajaRechazosYBajaPermitidaDelUltimoFuturo() throws Exception {
        rechaza(Codigo.NO_FUTURO, () -> { borrar.deleteEjercicio(15); return null; }); // inició en 2026 = año del reloj
        alta();
        alta();
        rechaza(Codigo.NO_ES_ULTIMO, () -> { borrar.deleteEjercicio(15); return null; });
        rechaza(Codigo.NO_ES_ULTIMO, () -> { borrar.deleteEjercicio(16); return null; });
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID IN (15, 16, 17)")).isEqualTo(3);
        var baja = http.perform(delete("/ejercicio/17"));
        assertThat(baja.andReturn().getResponse().getStatus()).isEqualTo(204);
        assertThat(json(baja)).isEmpty();
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 17")).isZero();
        assertThat(historiales(17)).isEqualTo(2); // alta y baja
        var inexistente = http.perform(delete("/ejercicio/99"));
        assertThat(inexistente.andReturn().getResponse().getStatus()).isEqualTo(404);
        assertThat(json(inexistente)).contains("NO_ENCONTRADO");
    }

    @Test
    void bajaConFilaDeComprasEsEnUsoPorLaFkRealYNoDejaHistorialDeBaja() throws Exception {
        alta();
        ejecutar("INSERT INTO compra_referencia (ejercicio_id, importe, created) VALUES (16, " + IMPORTE_MARCADO + ", NOW())");
        rechaza(Codigo.EN_USO, () -> { borrar.deleteEjercicio(16); return null; });
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 16")).isEqualTo(1);
        assertThat(historiales(16)).as("solo el alta: la baja revirtió").isEqualTo(1);
        ejecutar("DELETE FROM compra_referencia WHERE ejercicio_id = 16 AND importe = " + IMPORTE_MARCADO);
        borrar.deleteEjercicio(16);
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 16")).isZero();
    }

    @Test
    void bajaConMovimientoMarcadoEnElPeriodoEsEnUso() throws Exception {
        alta();
        movimientoMarcado("2027-09-15");
        rechaza(Codigo.EN_USO, () -> { borrar.deleteEjercicio(16); return null; });
        assertThat(uso.hayMovimientos(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-29"))).isTrue();
        // Borde: un movimiento el último día del período también cuenta; el día siguiente ya no.
        ejecutar("DELETE FROM movcon WHERE MCo_Concepto LIKE '" + PREFIJO + "%'");
        movimientoMarcado("2028-02-29");
        assertThat(uso.hayMovimientos(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-29"))).isTrue();
        ejecutar("DELETE FROM movcon WHERE MCo_Concepto LIKE '" + PREFIJO + "%'");
        movimientoMarcado("2028-03-01");
        assertThat(uso.hayMovimientos(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-29"))).isFalse();
    }

    @Test
    void bajaConAsientoSinMovimientosEnElPeriodoEsEnUso() throws Exception {
        alta();
        asientoMarcado("2027-09-15"); // D14: un asiento sin movimientos también bloquea
        assertThat(contar("SELECT COUNT(*) FROM movcon WHERE MCo_Fecha = '2027-09-15' AND MCo_NroComp = " + ORDEN_RESERVADO)).isZero();
        rechaza(Codigo.EN_USO, () -> { borrar.deleteEjercicio(16); return null; });
        ejecutar("DELETE FROM asiento WHERE vinculo LIKE '" + PREFIJO + "%'");
        borrar.deleteEjercicio(16);
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 16")).isZero();
    }

    // ---------------------------------------------------------------- bloqueo y PUT

    @Test
    void bloqueoYDesbloqueoPorApiConservanTodoLoDemasYElPayloadCompletoDeVb6Coincide() throws Exception {
        Ejercicio e16 = alta();
        ejecutar("UPDATE ejercicios SET orden_contable_resultado = 7, orden_contable_bienes_uso = 9 WHERE Eje_ID = 16");
        long historialInicial = historiales(16);
        var bloquea = http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":1}"));
        assertThat(bloquea.andReturn().getResponse().getStatus()).isEqualTo(200);
        assertThat(json(bloquea)).contains("\"bloqueado\":1", "\"ordenContableResultado\":7", "\"ordenContableBienesUso\":9",
                "\"fechaInicio\":\"2027-03-01T00:00:00Z\"", "\"fechaFinal\":\"2028-02-29T00:00:00Z\"");
        assertThat(texto("SELECT CONCAT_WS('|', Eje_Nombre, Eje_FechaInicio, Eje_FechaFin, Eje_Bloqueado, orden_contable_resultado, orden_contable_bienes_uso)"
                + " FROM ejercicios WHERE Eje_ID = 16"))
                .isEqualTo("Ejercicio Económico 2027|2027-03-01 00:00:00|2028-02-29 00:00:00|1|7|9");
        assertThat(historiales(16)).isEqualTo(historialInicial + 1);
        // makeParams de VB6 reenvía todos los campos; igual a lo guardado se acepta y desbloquea.
        String vb6 = """
                {"ejercicioId":16,"nombre":"Ejercicio Económico 2027","fechaInicio":"2027-03-01T00:00:00Z",
                 "fechaFinal":"2028-02-29T00:00:00Z","bloqueado":0,"ordenContableResultado":7,"ordenContableBienesUso":9}""";
        var desbloquea = http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content(vb6));
        assertThat(desbloquea.andReturn().getResponse().getStatus()).isEqualTo(200);
        assertThat(texto("SELECT Eje_Bloqueado FROM ejercicios WHERE Eje_ID = 16")).isEqualTo("0");
        // Sin cambio no deja historial; bloquear lo ya bloqueado tampoco.
        long antes = historiales(16);
        http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content(vb6));
        assertThat(historiales(16)).isEqualTo(antes);
        // Nombre o fechas distintos: 409 CAMPO_INMUTABLE; 404 y 400 con cuerpo.
        var inmutable = http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON)
                .content("{\"fechaInicio\":\"2027-04-01T00:00:00Z\"}"));
        assertThat(inmutable.andReturn().getResponse().getStatus()).isEqualTo(409);
        assertThat(json(inmutable)).contains("CAMPO_INMUTABLE");
        var inexistente = http.perform(put("/ejercicio/99").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":1}"));
        assertThat(inexistente.andReturn().getResponse().getStatus()).isEqualTo(404);
        var invalido = http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":2}"));
        assertThat(invalido.andReturn().getResponse().getStatus()).isEqualTo(400);
        assertThat(json(invalido)).contains("SOLICITUD_INVALIDA");
        var malformado = http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content("{no es json"));
        assertThat(malformado.andReturn().getResponse().getStatus()).isEqualTo(400);
        assertThat(e16.getEjercicioId()).isEqualTo(16);
    }

    @Test
    void elFlagBloqueadoDetieneSaveAsientoYDeleteAsientoDesdeEnContabilidadService() throws Exception {
        alta();
        // Real: el flag leído de MySQL por EjercicioService legacy y AsientoService con su repositorio JPA. Simulado: el resto.
        var contabilidad = new ContabilidadService(new AsientoService(asientosJpa), null, new EjercicioService(repositorioLegacy),
                mock(CuentaMovimientoService.class), null, null, null, null);
        OffsetDateTime fecha = OffsetDateTime.parse("2027-09-15T00:00:00Z");
        http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":1}"));
        assertThatThrownBy(() -> transaction.execute(status -> {
            contabilidad.saveAsiento(fecha, ORDEN_RESERVADO, null, MARCA, List.of());
            return null;
        })).isInstanceOf(EjercicioBloqueadoException.class);
        assertThat(contar("SELECT COUNT(*) FROM asiento WHERE vinculo = '" + MARCA + "'")).isZero();
        asientoMarcado("2027-09-15");
        Boolean conBloqueo = transaction.execute(status -> contabilidad.deleteAsientoDesde(fecha, ORDEN_RESERVADO, 1));
        assertThat(conBloqueo).isFalse();
        assertThat(contar("SELECT COUNT(*) FROM asiento WHERE vinculo = '" + MARCA + "'")).as("no se borró con el ejercicio bloqueado").isEqualTo(1);
        http.perform(put("/ejercicio/16").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":0}"));
        Boolean sinBloqueo = transaction.execute(status -> contabilidad.deleteAsientoDesde(fecha, ORDEN_RESERVADO, 1));
        assertThat(sinBloqueo).isTrue();
        assertThat(contar("SELECT COUNT(*) FROM asiento WHERE vinculo = '" + MARCA + "'")).as("desbloqueado, se borra").isZero();
    }

    // ---------------------------------------------------------------- concurrencia

    static void pausarAntesDe(String prefijo) {
        enPausa = new CountDownLatch(1);
        reanudar = new CountDownLatch(1);
        pausarEn = sql -> sql.startsWith(prefijo);
    }

    /** Sentencia de la transacción en LOCK WAIT, o null si ninguna esperó dentro del plazo. */
    static String esperaEn(Future<?> tarea, long segundos) throws Exception {
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(segundos);
        while (System.nanoTime() < limite && !tarea.isDone()) {
            String consulta = texto("SELECT LOWER(trx_query) FROM information_schema.INNODB_TRX WHERE trx_state = 'LOCK WAIT' LIMIT 1");
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

    /** Mutante: lee sin bloqueo el último ejercicio y la fila editada; el resto delega en el adaptador real. */
    EjercicioRepository mutante() {
        return new EjercicioRepository() {
            @Override
            public java.util.Optional<Ejercicio> findLastWithLock() {
                return entityManager.createQuery("select e from EjercicioEntity e order by e.ejercicioId desc", EjercicioEntity.class)
                        .setMaxResults(1).getResultList().stream().findFirst().map(mapper::toDomain);
            }

            @Override
            public java.util.Optional<Ejercicio> findWithLockByEjercicioId(Integer id) {
                return java.util.Optional.ofNullable(entityManager.find(EjercicioEntity.class, id)).map(mapper::toDomain);
            }

            @Override
            public boolean existsByEjercicioId(Integer id) { return repositorio.existsByEjercicioId(id); }

            @Override
            public Ejercicio create(Ejercicio e) { return repositorio.create(e); }

            @Override
            public Ejercicio update(Ejercicio e) { return repositorio.update(e); }

            @Override
            public void deleteByEjercicioId(Integer id) { repositorio.deleteByEjercicioId(id); }
        };
    }

    @Test
    void dosAltasConElMismoIdSeSerializanPorElLockDelUltimo() throws Exception {
        Callable<Object> altaConId16 = () -> crear.createEjercicio(Ejercicio.builder().ejercicioId(16).build());
        var cruce = cruzar("insert into ejercicios", altaConId16, altaConId16, 30);
        assertThat(cruce.esperaDeB()).as("B espera el bloqueo del último ejercicio").contains("for update");
        // InnoDB puede revertir por deadlock a una de las dos (gap lock del final del índice): la repetición ve el 16
        // ya confirmado. Ganó una sola y la otra recibe DUPLICADO, nunca un error de base de datos ni dos filas.
        var errores = java.util.stream.Stream.of(cruce.errorA(), cruce.errorB()).filter(java.util.Objects::nonNull).toList();
        assertThat(errores).hasSize(1);
        assertThat(errores.getFirst()).isInstanceOfSatisfying(EjercicioConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 16")).isEqualTo(1);
        assertThat(historiales(16)).isEqualTo(1);
    }

    @Test
    void mutanteSinLockDejaQueLaSegundaAltaLleguePorElInsertYPierdaPorLaPk() throws Exception {
        var sinLock = new CreateEjercicioUseCaseImpl(mutante(), historial, transaction, clock);
        Callable<Object> altaConId16 = () -> sinLock.createEjercicio(Ejercicio.builder().ejercicioId(16).build());
        var cruce = cruzar("insert into ejercicios", altaConId16, altaConId16, 30);
        // Sin el lock B no espera a A: lee el mismo último, inserta primero y A choca después con la PK.
        assertThat(cruce.esperaDeB()).as("el mutante no espera ningún bloqueo").isNull();
        assertThat(cruce.errorB()).isNull();
        assertThat(cruce.errorA()).isInstanceOfSatisfying(EjercicioConflictoException.class,
                e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
    }

    @Test
    void altaContraBajaNoDejanHuecoEnLaSecuencia() throws Exception {
        alta(); // 16
        var cruce = cruzar("delete from ejercicios", () -> { borrar.deleteEjercicio(16); return null; }, this::alta, 30);
        assertThat(cruce.esperaDeB()).as("el alta espera el bloqueo del último").contains("for update");
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
        // B vio el 15 como último y creó el 16: sin hueco (con el 17 sin el 16 la secuencia quedaría rota).
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 16")).isEqualTo(1);
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 17")).isZero();
    }

    @Test
    void mutanteSinLockDejaHuecoEnAltaContraBaja() throws Exception {
        alta(); // 16
        var sinLock = new CreateEjercicioUseCaseImpl(mutante(), historial, transaction, clock);
        var bajaSinLock = new DeleteEjercicioUseCaseImpl(mutante(), (d, h) -> false, historial, transaction, clock);
        var cruce = cruzar("delete from ejercicios", () -> { bajaSinLock.deleteEjercicio(16); return null; },
                () -> sinLock.createEjercicio(Ejercicio.builder().build()), 5);
        assertThat(cruce.esperaDeB()).as("sin lock B no espera").isNull();
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 16")).as("hueco: el 16 se borró").isZero();
        assertThat(contar("SELECT COUNT(*) FROM ejercicios WHERE Eje_ID = 17")).as("y el 17 existe").isEqualTo(1);
    }

    @Test
    void dosPutSobreElMismoEjercicioPersistenAmbosCambios() throws Exception {
        alta();
        var cruce = cruzar("update ejercicios",
                () -> editar.updateEjercicio(16, Ejercicio.builder().bloqueado((byte) 1).build()),
                () -> editar.updateEjercicio(16, Ejercicio.builder().ordenContableResultado(7).build()), 30);
        assertThat(cruce.esperaDeB()).as("B espera el bloqueo de la fila editada").contains("for update");
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
        assertThat(texto("SELECT CONCAT_WS('|', Eje_Bloqueado, orden_contable_resultado) FROM ejercicios WHERE Eje_ID = 16")).isEqualTo("1|7");
    }

    @Test
    void mutanteSinLockNoSerializaDosPut() throws Exception {
        alta();
        var sinLock = new UpdateEjercicioUseCaseImpl(mutante(), historial, transaction);
        var cruce = cruzar("update ejercicios",
                () -> sinLock.updateEjercicio(16, Ejercicio.builder().bloqueado((byte) 1).build()),
                () -> sinLock.updateEjercicio(16, Ejercicio.builder().ordenContableResultado(7).build()), 5);
        // Sin el lock B no espera a A: parte de lo mismo que leyó A y la fusión de nulos queda a ciegas de la otra escritura.
        // (La entidad usa @DynamicUpdate: en columnas distintas el resultado final coincide, así que el valor
        // final no distingue al mutante; lo que lo distingue es que no hay espera.)
        assertThat(cruce.esperaDeB()).as("sin lock B no espera").isNull();
        assertThat(cruce.errorA()).isNull();
        assertThat(cruce.errorB()).isNull();
    }
}
