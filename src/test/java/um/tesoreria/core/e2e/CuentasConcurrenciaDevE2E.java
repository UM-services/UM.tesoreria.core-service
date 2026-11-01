package um.tesoreria.core.e2e;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import jakarta.persistence.EntityManager;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.application.usecases.*;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaJerarquia;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.adapter.CuentaHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository.JpaCuentaRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;
import um.tesoreria.core.service.view.CuentaSearchService;
import static org.assertj.core.api.Assertions.*;

/**
 * Concurrencia real contra MySQL de desarrollo (#407). Opt-in: escribe en tablas reales, solo en la raíz 9 y en un
 * comprobante sintético reservado, con filas marcadas {@code E2E-407-*}.
 * <p>
 * Cada caso fuerza el cruce que dice probar: una operación queda pausada (inspector de SQL) o bloqueada por otra
 * conexión, y se comprueba en {@code information_schema} que la otra queda en {@code LOCK WAIT} antes de soltarla.
 * <p>
 * Limpieza: al empezar recupera residuos marcados de corridas interrumpidas; al terminar borra lo propio. La foto
 * global (CHECKSUM, filas, AUTO_INCREMENT) se informa pero no hace fallar la prueba: la base de dev es compartida.
 * Ejecutar a pedido con E2E_DEV_ESCRITURA=si, IT_DB_* y -Dit.test=CuentasConcurrenciaDevE2E.
 */
@DataJpaTest(properties = {"spring.jpa.show-sql=false"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "E2E_DEV_ESCRITURA", matches = "si")
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateCuentaUseCaseImpl.class, UpdateCuentaUseCaseImpl.class, DeleteCuentaUseCaseImpl.class,
        RecalculaGradosUseCaseImpl.class, JpaCuentaRepositoryAdapter.class, CuentaMapper.class,
        CuentaHistorialAdapter.class, EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class, CuentasConcurrenciaDevE2E.Auditoria.class})
class CuentasConcurrenciaDevE2E {
    static final BigDecimal RAIZ = new BigDecimal("90000000000");
    static final BigDecimal PADRE = new BigDecimal("90100000000");
    static final BigDecimal HIJA = new BigDecimal("90101000000");
    static final BigDecimal HERMANA = new BigDecimal("90102000000");
    static final String PREFIJO = "E2E-407-";
    static final String MARCA = PREFIJO + UUID.randomUUID().toString().substring(0, 8);
    /** Comprobante sintético: nada lo referencia (p. ej. la FK de valores), así que la limpieza siempre puede borrarlo. */
    static final String FECHA_RESERVADA = "1901-01-01";
    static final int COMPROBANTE_RESERVADO = 9407; // MCo_NroComp es smallint
    static final String HILO = "cuenta407";
    static final int ESPERA_BLOQUEO_SEGUNDOS = 60;

    static Reserva reserva;
    /** Pausa el hilo {@link #HILO} justo antes de la primera sentencia que cumpla la condición. */
    static volatile Predicate<String> pausarEn;
    static volatile CountDownLatch enPausa;
    static volatile CountDownLatch reanudar;

    @Autowired CreateCuentaUseCaseImpl crear;
    @Autowired UpdateCuentaUseCaseImpl editar;
    @Autowired DeleteCuentaUseCaseImpl borrar;
    @Autowired RecalculaGradosUseCaseImpl recalcular;
    @Autowired CuentaRepository cuentas;
    @Autowired TransactionOperations transaction;
    @Autowired EntityManager entityManager;
    @MockitoBean CuentaSearchService search;

    @TestConfiguration
    @EnableJpaAuditing
    static class Auditoria { }

    static String url() {
        return "jdbc:mysql://%s:%s/%s?sslMode=REQUIRED&serverTimezone=UTC&connectTimeout=60000&socketTimeout=300000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium"));
    }

    @DynamicPropertySource
    static void dev(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", CuentasConcurrenciaDevE2E::url);
        r.add("spring.datasource.username", () -> System.getenv("IT_DB_USER"));
        r.add("spring.datasource.password", () -> System.getenv("IT_DB_PASSWORD"));
        r.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        r.add("spring.datasource.hikari.maximum-pool-size", () -> "4");
        r.add("spring.datasource.hikari.connection-init-sql",
                () -> "SET SESSION innodb_lock_wait_timeout=" + ESPERA_BLOQUEO_SEGUNDOS);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        r.add("spring.jpa.database-platform", () -> "um.tesoreria.core.configuration.MySQL57Dialect");
        r.add("spring.jpa.properties.hibernate.session_factory.statement_inspector", Inspector.class::getName);
    }

    public static class Inspector implements StatementInspector {
        private static final java.util.regex.Pattern ESCRITURA = java.util.regex.Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete(?:\\s+\\w+)?\\s+from)\\s+`?(\\w+)`?");

        @Override
        public String inspect(String sql) {
            String normal = sql.toLowerCase(java.util.Locale.ROOT).stripLeading();
            var escritura = ESCRITURA.matcher(normal);
            if (escritura.find() && !List.of("plancta", "gestion_escritura_historial").contains(escritura.group(1))) {
                throw new IllegalStateException("Escritura fuera del flujo de cuentas");
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

    // ---------------------------------------------------------------- ciclo de vida

    @BeforeAll
    static void reservar() throws Exception {
        reserva = new Reserva();
        reserva.abrir();
    }

    @AfterAll
    static void cerrar() throws Exception {
        if (reserva != null) reserva.close();
    }

    @BeforeEach
    void preparar() throws Exception {
        reserva.barrer();
        crear.createCuenta(cuenta(RAIZ));
        crear.createCuenta(cuenta(PADRE));
    }

    @AfterEach
    void soltarPausa() {
        liberar();
    }

    // ---------------------------------------------------------------- ayudas

    static Cuenta cuenta(BigDecimal numero) {
        return cuenta(numero, MARCA);
    }

    static Cuenta cuenta(BigDecimal numero, String nombre) {
        return Cuenta.builder().numeroCuenta(numero).nombre(nombre).visible((byte) 1).integradora((byte) 0).build();
    }

    static void pausarAntesDe(String prefijo) {
        enPausa = new CountDownLatch(1);
        reanudar = new CountDownLatch(1);
        pausarEn = sql -> sql.startsWith(prefijo);
    }

    static void pausarSi(Predicate<String> condicion) {
        enPausa = new CountDownLatch(1);
        reanudar = new CountDownLatch(1);
        pausarEn = condicion;
    }

    /** Suelta cualquier pausa; se llama en finally antes de cerrar executors para no retener bloqueos. */
    static void liberar() {
        pausarEn = null;
        if (reanudar != null) reanudar.countDown();
    }

    static ExecutorService hilo407() {
        return Executors.newSingleThreadExecutor(r -> new Thread(r, HILO));
    }

    static Connection conexionDirecta() throws Exception {
        Connection db = DriverManager.getConnection(url(), System.getenv("IT_DB_USER"), System.getenv("IT_DB_PASSWORD"));
        try (var st = db.createStatement()) {
            st.execute("SET SESSION innodb_lock_wait_timeout=" + ESPERA_BLOQUEO_SEGUNDOS);
        }
        return db;
    }

    static long idConexion(Connection db) throws Exception {
        try (var st = db.createStatement(); var rs = st.executeQuery("SELECT CONNECTION_ID()")) {
            rs.next();
            return rs.getLong(1);
        }
    }

    /** Id de la conexión de la transacción JPA en curso. */
    long idConexionActual() {
        return ((Number) entityManager.createNativeQuery("SELECT CONNECTION_ID()").getSingleResult()).longValue();
    }

    /** Espera hasta ver alguna transacción en LOCK WAIT detrás de la conexión dada, o hasta que termine la tarea. */
    static boolean hayEsperaDetrasDe(long conexionBloqueante, Future<?> tarea) throws Exception {
        return esperarHasta(tarea, "SELECT COUNT(*) FROM information_schema.INNODB_LOCK_WAITS w"
                + " JOIN information_schema.INNODB_TRX b ON b.trx_id = w.blocking_trx_id"
                + " WHERE b.trx_mysql_thread_id = " + conexionBloqueante);
    }

    /** Espera hasta ver la conexión dada en LOCK WAIT, o hasta que termine la tarea. */
    static boolean conexionEnEspera(AtomicLong conexion, Future<?> tarea) throws Exception {
        long limite = System.nanoTime() + TimeUnit.MINUTES.toNanos(5);
        while (System.nanoTime() < limite && !tarea.isDone()) {
            if (conexion.get() > 0 && reserva.contar("SELECT COUNT(*) FROM information_schema.INNODB_TRX"
                    + " WHERE trx_state='LOCK WAIT' AND trx_mysql_thread_id=" + conexion.get()) > 0) return true;
            Thread.sleep(100);
        }
        return false;
    }

    /** Espera hasta ver una transacción en LOCK WAIT ejecutando una sentencia que contenga el fragmento. */
    static boolean hayEsperaEn(String fragmentoSql, Future<?> tarea) throws Exception {
        return esperarHasta(tarea, "SELECT COUNT(*) FROM information_schema.INNODB_TRX WHERE trx_state='LOCK WAIT'"
                + " AND LOWER(trx_query) LIKE '%" + fragmentoSql + "%'");
    }

    private static boolean esperarHasta(Future<?> tarea, String consulta) throws Exception {
        long limite = System.nanoTime() + TimeUnit.MINUTES.toNanos(5);
        while (System.nanoTime() < limite && !tarea.isDone()) {
            if (reserva.contar(consulta) > 0) return true;
            Thread.sleep(100);
        }
        return false;
    }

    static String describir(Future<?> tarea) {
        return "terminó " + tarea.state()
                + (tarea.state() == Future.State.FAILED ? ": " + tarea.exceptionNow() : "");
    }

    /** Asiento de otro escritor en el comprobante reservado; copia tipo de comprobante y demás datos de un asiento real. */
    static int insertarAsiento(BigDecimal cuenta, AtomicLong conexion) throws Exception {
        try (var db = conexionDirecta()) {
            conexion.set(idConexion(db));
            String sql = "INSERT INTO movcon (mco_fecha,mco_nrocomp,mco_item,mco_pla_cuenta,mco_debita,"
                    + "mco_tco_id,mco_concepto,mco_importe,mco_prv_id,mco_nroanulado,version,apertura,created) "
                    + "SELECT '" + FECHA_RESERVADA + "'," + COMPROBANTE_RESERVADO + ","
                    + "(SELECT COALESCE(MAX(x.mco_item),0)+1 FROM movcon x WHERE x.mco_fecha='" + FECHA_RESERVADA
                    + "' AND x.mco_nrocomp=" + COMPROBANTE_RESERVADO + "),"
                    + "?,m.mco_debita,m.mco_tco_id,?,m.mco_importe,NULL,m.mco_nroanulado,m.version,m.apertura,NOW() "
                    + "FROM movcon m ORDER BY m.id LIMIT 1";
            try (var st = db.prepareStatement(sql)) {
                st.setBigDecimal(1, cuenta);
                st.setString(2, MARCA);
                return st.executeUpdate();
            }
        }
    }

    // ---------------------------------------------------------------- casos

    @Test
    void bajaDePadreGanaYAltaEsperaLuegoRechazaSinHuerfana() throws Exception {
        try (var db = conexionDirecta(); var alta407 = hilo407()) {
            db.setAutoCommit(false);
            try {
                try (var st = db.prepareStatement("SELECT pla_cuenta FROM plancta WHERE pla_cuenta=? FOR UPDATE")) {
                    st.setBigDecimal(1, PADRE);
                    try (var rs = st.executeQuery()) { assertThat(rs.next()).isTrue(); }
                }
                var alta = alta407.submit(() -> crear.createCuenta(cuenta(HIJA)));
                // El alta tiene que quedar esperando el bloqueo del padre, no leerlo sin bloquear.
                assertThat(hayEsperaDetrasDe(idConexion(db), alta)).as(() -> "el alta espera el bloqueo del padre; " + describir(alta)).isTrue();
                try (var st = db.prepareStatement("DELETE FROM plancta WHERE pla_cuenta=?")) {
                    st.setBigDecimal(1, PADRE);
                    st.executeUpdate();
                }
                db.commit();
                assertThatThrownBy(() -> alta.get(5, TimeUnit.MINUTES))
                        .hasCauseInstanceOf(CuentaConflictoException.class)
                        .hasMessageContaining("Falta la cuenta ancestro " + PADRE.toPlainString());
                assertThat(cuentas.findById(HIJA)).isEmpty();
            } finally {
                db.rollback();
            }
        }
    }

    @Test
    void altaDeHijaGanaYBajaEsperaLuegoRechazaConHijas() throws Exception {
        try (var baja407 = hilo407()) {
            var baja = transaction.execute(status -> {
                cuentas.findWithLockByNumeroCuenta(PADRE).orElseThrow();
                long bloqueante = idConexionActual();
                var futuro = baja407.submit(() -> borrar.deleteCuenta(PADRE));
                try {
                    assertThat(hayEsperaDetrasDe(bloqueante, futuro)).as(() -> "la baja espera el bloqueo; " + describir(futuro)).isTrue();
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
                crear.createCuenta(cuenta(HIJA)); // misma transacción: confirma junto con el bloqueo
                return futuro;
            });
            assertThatThrownBy(() -> baja.get(5, TimeUnit.MINUTES))
                    .hasCauseInstanceOf(CuentaConflictoException.class)
                    .hasMessageContaining("tiene cuentas hijas");
            assertThat(cuentas.findById(PADRE)).isPresent();
            assertThat(cuentas.findById(HIJA)).isPresent();
        }
    }

    @Test
    void asientoEnLaVentanaDeLaBajaEsperaAlBloqueoYLaFkLoRechaza() throws Exception {
        // La baja ya controló hijas y movimientos; se pausa justo antes del DELETE.
        pausarAntesDe("delete from plancta");
        try (var baja407 = hilo407(); var otro = Executors.newSingleThreadExecutor()) {
            try {
                var baja = baja407.submit(() -> borrar.deleteCuenta(PADRE));
                assertThat(enPausa.await(5, TimeUnit.MINUTES)).isTrue();
                var conexion = new AtomicLong();
                var asiento = otro.submit(() -> insertarAsiento(PADRE, conexion));
                // El asiento necesita la fila del padre (FK) y la baja la tiene bloqueada: tiene que quedar en LOCK WAIT.
                boolean esperando = conexionEnEspera(conexion, asiento);
                assertThat(baja.isDone()).as("la baja sigue en pausa mientras se observa el asiento").isFalse();
                assertThat(asiento.isDone()).as(() -> "el asiento no puede confirmar mientras la baja tiene el padre; "
                        + describir(asiento)).isFalse();
                assertThat(esperando).as("el asiento espera el bloqueo de la baja").isTrue();
                liberar();
                baja.get(5, TimeUnit.MINUTES);
                assertThatThrownBy(() -> asiento.get(5, TimeUnit.MINUTES))
                        .hasCauseInstanceOf(java.sql.SQLIntegrityConstraintViolationException.class)
                        .hasStackTraceContaining("movcon_ibfk_2");
                assertThat(cuentas.findById(PADRE)).isEmpty();
            } finally {
                liberar();
            }
        }
    }

    @Test
    void referenciaConFkEnOtraTablaDevuelve409SinCambios() throws Exception {
        // cuenta_movimiento_saldo no la mira el control de movimientos: solo la FK real la frena.
        reserva.ejecutar("INSERT INTO cuenta_movimiento_saldo (cuenta) VALUES (" + PADRE.toPlainString() + ")");
        String bajas = "SELECT COUNT(*) FROM gestion_escritura_historial WHERE entidad='cuenta' AND operacion='BAJA'"
                + " AND CAST(entidad_clave AS DECIMAL(11,0))>=90000000000";
        long bajasAntes = reserva.contar(bajas);
        assertThatThrownBy(() -> borrar.deleteCuenta(PADRE))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("registros asociados");
        assertThat(cuentas.findById(PADRE)).isPresent();
        assertThat(reserva.contar(bajas)).as("la baja rechazada no deja evento de historial").isEqualTo(bajasAntes);
    }

    @Test
    void bajaDuranteElRecalculoNoReinsertaLaCuenta() throws Exception {
        Assumptions.assumeTrue(reserva.gradosRealesConsistentes(),
                "hay cuentas reales con grados por corregir: el recálculo escribiría fuera de la raíz 9");
        reserva.ejecutar("UPDATE plancta SET pla_grado=0 WHERE pla_cuenta=" + PADRE.toPlainString());
        // Pausa antes de escribir el padre: en el UPDATE del recálculo gestionado, o en la relectura por id que
        // haría un merge de objetos desconectados (el código anterior), que tras la baja terminaba en INSERT.
        pausarSi(sql -> sql.startsWith("update plancta")
                || (sql.startsWith("select") && sql.contains(" from plancta ") && sql.contains(" where ")));
        try (var recalculo407 = hilo407()) {
            try {
                var recalculo = recalculo407.submit(() -> recalcular.recalculaGrados());
                assertThat(enPausa.await(5, TimeUnit.MINUTES)).isTrue();
                reserva.ejecutar("DELETE FROM plancta WHERE pla_cuenta=" + PADRE.toPlainString());
                liberar();
                assertThatThrownBy(() -> recalculo.get(5, TimeUnit.MINUTES))
                        .hasCauseInstanceOf(ConcurrencyFailureException.class);
                assertThat(cuentas.findById(PADRE)).as("el recálculo no vuelve a insertar la cuenta borrada").isEmpty();
            } finally {
                liberar();
            }
        }
    }

    @Test
    void edicionDuranteElRecalculoNoSePierde() throws Exception {
        Assumptions.assumeTrue(reserva.gradosRealesConsistentes(),
                "hay cuentas reales con grados por corregir: el recálculo escribiría fuera de la raíz 9");
        reserva.ejecutar("UPDATE plancta SET pla_grado=0 WHERE pla_cuenta=" + PADRE.toPlainString());
        String editado = MARCA + "-editado";
        pausarAntesDe("update plancta");
        try (var recalculo407 = hilo407()) {
            try {
                var recalculo = recalculo407.submit(() -> recalcular.recalculaGrados());
                // El recálculo ya leyó todas las cuentas (con el nombre viejo) y está por escribir el padre.
                assertThat(enPausa.await(5, TimeUnit.MINUTES)).isTrue();
                Cuenta pedido = Cuenta.builder().nombre(editado).visible((byte) 0).build();
                assertThat(editar.updateCuenta(PADRE, pedido)).isPresent();
                liberar();
                recalculo.get(5, TimeUnit.MINUTES);
                Cuenta padre = cuentas.findById(PADRE).orElseThrow();
                assertThat(padre.getNombre()).as("el recálculo no pisa el nombre editado").isEqualTo(editado);
                assertThat(padre.getVisible()).as("el recálculo no pisa visible").isEqualTo((byte) 0);
                assertThat(padre.getGrado()).isEqualTo(2);
                assertThat(reserva.contar("SELECT COUNT(*) FROM gestion_escritura_historial WHERE entidad='cuenta'"
                        + " AND operacion='EDICION' AND entidad_clave='" + PADRE.toPlainString() + "'")).isEqualTo(1);
            } finally {
                liberar();
            }
        }
    }

    @Test
    void dosAltasHermanasSeSerializanYEntranAmbas() throws Exception {
        // La primera alta queda pausada con los ancestros bloqueados; la segunda tiene que esperarla.
        pausarAntesDe("insert into plancta");
        try (var primera407 = hilo407(); var otro = Executors.newSingleThreadExecutor()) {
            try {
                var primera = primera407.submit(() -> crear.createCuenta(cuenta(HIJA)));
                assertThat(enPausa.await(5, TimeUnit.MINUTES)).isTrue();
                var segunda = otro.submit(() -> crear.createCuenta(cuenta(HERMANA)));
                assertThat(hayEsperaEn("for update", segunda)).as(() -> "la segunda alta espera los ancestros; " + describir(segunda)).isTrue();
                liberar();
                primera.get(5, TimeUnit.MINUTES);
                segunda.get(5, TimeUnit.MINUTES);
                assertThat(cuentas.findById(HIJA)).isPresent();
                assertThat(cuentas.findById(HERMANA)).isPresent();
                assertThat(cuentas.findById(PADRE).orElseThrow().getIntegradora()).isEqualTo((byte) 1);
            } finally {
                liberar();
            }
        }
    }

    @Test
    void dosAltasDeLaMismaRaizSoloInsertaUnaSinPisarla() throws Exception {
        borrar.deleteCuenta(PADRE);
        borrar.deleteCuenta(RAIZ);
        String nombreA = MARCA + "-A";
        String nombreB = MARCA + "-B";
        // La primera alta queda pausada justo antes del INSERT; la segunda llega a su INSERT y queda esperando.
        pausarAntesDe("insert into plancta");
        try (var primera407 = hilo407(); var otro = Executors.newSingleThreadExecutor()) {
            try {
                Callable<String> altaA = () -> { crear.createCuenta(cuenta(RAIZ, nombreA)); return nombreA; };
                Callable<String> altaB = () -> { crear.createCuenta(cuenta(RAIZ, nombreB)); return nombreB; };
                var primera = primera407.submit(altaA);
                assertThat(enPausa.await(5, TimeUnit.MINUTES)).isTrue();
                var segunda = otro.submit(altaB);
                assertThat(hayEsperaEn("insert into plancta", segunda)).as(() -> "la segunda alta queda en espera; " + describir(segunda)).isTrue();
                liberar();
                List<String> ganadores = new java.util.ArrayList<>();
                for (var alta : List.of(primera, segunda)) {
                    try {
                        ganadores.add(alta.get(5, TimeUnit.MINUTES));
                    } catch (java.util.concurrent.ExecutionException ex) {
                        assertThat(ex).hasCauseInstanceOf(CuentaConflictoException.class);
                    }
                }
                assertThat(ganadores).hasSize(1);
                assertThat(cuentas.findById(RAIZ).orElseThrow().getNombre())
                        .as("la raíz guardada es la del alta que ganó, sin pisar").isEqualTo(ganadores.getFirst());
            } finally {
                liberar();
            }
        }
    }

    // ---------------------------------------------------------------- reserva y limpieza

    /**
     * Reserva la raíz 9 con GET_LOCK, recupera residuos marcados de corridas interrumpidas y limpia solo filas
     * propias: cuentas marcadas y lo que cuelga de ellas (movimientos, saldos, historial). Informa la foto global.
     */
    static final class Reserva implements AutoCloseable {
        static final List<String> TABLAS = List.of("plancta", "movcon", "cuenta_movimiento_saldo", "gestion_escritura_historial");
        /** Cuentas de la raíz 9 que no son de esta prueba: si hay alguna, no se toca nada. */
        static final String AJENAS = "SELECT COUNT(*) FROM plancta WHERE pla_cuenta>=90000000000 AND pla_nombre NOT LIKE '" + PREFIJO + "%'";
        /** Números de la raíz 9 que pertenecen a pruebas: marcados o ya borrados (sin fila en plancta). */
        static final String NO_AJENA = "NOT IN (SELECT pla_cuenta FROM plancta WHERE pla_cuenta>=90000000000 AND pla_nombre NOT LIKE '"
                + PREFIJO + "%')";

        Connection db;
        Map<String, Long> foto;
        final Map<String, Long> contadores = new LinkedHashMap<>();

        void abrir() throws Exception {
            db = conexionDirecta();
            assertThat(contar("SELECT GET_LOCK('it407-raiz9', 0)")).as("raíz 9 reservada por otra corrida").isEqualTo(1);
            assertThat(contar(AJENAS)).as("la raíz 9 tiene cuentas que no son de prueba").isZero();
            barrer(); // recupera residuos de corridas interrumpidas
            for (String tabla : TABLAS) {
                try (var st = db.createStatement(); var rs = st.executeQuery("SHOW TABLE STATUS LIKE '" + tabla + "'")) {
                    rs.next();
                    contadores.put(tabla, rs.getLong("Auto_increment"));
                }
            }
            foto = checksums();
            System.out.println("E2E407 antes: " + foto + "; AUTO_INCREMENT=" + contadores);
        }

        void ejecutar(String sql) throws Exception {
            try (var st = db.createStatement()) {
                st.executeUpdate(sql);
            }
        }

        long contar(String sql) throws Exception {
            try (var st = db.createStatement(); var rs = st.executeQuery(sql)) {
                rs.next();
                return rs.getLong(1);
            }
        }

        void barrer() throws Exception {
            ejecutar("DELETE FROM movcon WHERE mco_concepto LIKE '" + PREFIJO + "%'"
                    + " AND (mco_pla_cuenta>=90000000000 OR (mco_fecha='" + FECHA_RESERVADA + "' AND mco_nrocomp=" + COMPROBANTE_RESERVADO + "))");
            ejecutar("DELETE FROM cuenta_movimiento_saldo WHERE cuenta>=90000000000 AND cuenta " + NO_AJENA);
            ejecutar("DELETE FROM gestion_escritura_historial WHERE entidad='cuenta'"
                    + " AND CAST(entidad_clave AS DECIMAL(11,0))>=90000000000 AND CAST(entidad_clave AS DECIMAL(11,0)) " + NO_AJENA);
            ejecutar("DELETE FROM plancta WHERE pla_cuenta>=90000000000 AND pla_nombre LIKE '" + PREFIJO + "%'");
        }

        /** Mismo criterio que el recálculo: los números fuera de la estructura se saltean. */
        boolean gradosRealesConsistentes() throws Exception {
            try (var st = db.createStatement(); var rs = st.executeQuery(
                    "SELECT pla_cuenta, pla_grado, pla_grado1, pla_grado2, pla_grado3, pla_grado4 FROM plancta WHERE pla_cuenta<90000000000")) {
                while (rs.next()) {
                    final CuentaJerarquia.Grados g;
                    try {
                        g = CuentaJerarquia.grados(rs.getBigDecimal(1));
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }
                    if (rs.getInt(2) != g.grado() || !igual(rs.getBigDecimal(3), g.grado1()) || !igual(rs.getBigDecimal(4), g.grado2())
                            || !igual(rs.getBigDecimal(5), g.grado3()) || !igual(rs.getBigDecimal(6), g.grado4())) {
                        return false;
                    }
                }
            }
            return true;
        }

        private static boolean igual(BigDecimal a, BigDecimal b) {
            return a != null && a.compareTo(b) == 0;
        }

        Map<String, Long> checksums() throws Exception {
            Map<String, Long> result = new LinkedHashMap<>();
            for (String tabla : TABLAS) {
                try (var st = db.createStatement(); var rs = st.executeQuery("CHECKSUM TABLE " + tabla)) {
                    rs.next();
                    result.put(tabla + ".checksum", rs.getLong(2));
                }
                result.put(tabla + ".filas", contar("SELECT COUNT(*) FROM " + tabla));
            }
            return result;
        }

        /** Vuelve el contador solo si nadie más insertó filas reales mientras corría la prueba. */
        private void restaurarContadores() throws Exception {
            Map<String, String> ids = Map.of("plancta", "id", "movcon", "id",
                    "cuenta_movimiento_saldo", "cuentamovimientosaldo_id", "gestion_escritura_historial", "escritura_historial_id");
            try (var st = db.createStatement()) {
                st.execute("SET SESSION lock_wait_timeout=5");
            }
            for (var contador : contadores.entrySet()) {
                long maximo = contar("SELECT COALESCE(MAX(" + ids.get(contador.getKey()) + "),0) FROM " + contador.getKey());
                if (maximo < contador.getValue()) {
                    try {
                        ejecutar("ALTER TABLE " + contador.getKey() + " AUTO_INCREMENT=" + contador.getValue());
                    } catch (java.sql.SQLException ex) {
                        System.out.println("E2E407 no se pudo restaurar AUTO_INCREMENT de " + contador.getKey() + ": " + ex.getMessage());
                    }
                }
            }
        }

        @Override
        public void close() throws Exception {
            try {
                if (foto != null) {
                    barrer();
                    restaurarContadores();
                    assertThat(contar("SELECT COUNT(*) FROM plancta WHERE pla_cuenta>=90000000000 AND pla_nombre LIKE '" + PREFIJO + "%'"))
                            .as("no quedan cuentas de prueba").isZero();
                    assertThat(contar("SELECT COUNT(*) FROM movcon WHERE mco_concepto LIKE '" + PREFIJO + "%'"))
                            .as("no quedan asientos de prueba").isZero();
                    Map<String, Long> despues = checksums();
                    System.out.println("E2E407 después: " + despues + (despues.equals(foto)
                            ? " — base idéntica a la de antes"
                            : " — difiere de la foto inicial (actividad ajena o contadores); no hace fallar la prueba"));
                }
            } finally {
                db.close(); // libera también GET_LOCK
            }
        }
    }
}
