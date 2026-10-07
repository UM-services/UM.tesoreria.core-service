package um.tesoreria.core.e2e;

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
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.CreateArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.DeleteArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.GetAllArticulosUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.GetArticuloByIdUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.GetNewArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.GetPaginatedArticulosUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.SearchArticulosUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.application.usecases.UpdateArticuloUseCaseImpl;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.CreateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.UpdateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter.JpaArticuloRepositoryAdapter;
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
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter.JpaUbicacionArticuloRepositoryAdapter;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Concurrencia real de dos escritores (#405) contra la base de desarrollo, en REPEATABLE READ como la de dev.
 * Otra conexión deja un cambio sin confirmar sobre la misma fila; el caso de uso, con su transacción real, queda
 * esperando el bloqueo; recién entonces la otra confirma. Así se reproduce sin azar lo que pasa con dos usuarios.
 * <p>
 * Escribe en tablas reales, por eso solo corre a pedido ({@code E2E_DEV_ESCRITURA=si} además de {@code IT_DB_*};
 * {@code -Dit.test=GastosConcurrenciaDevE2E}). Aislamiento y restauración: {@link DevDbReservas}.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "E2E_DEV_ESCRITURA", matches = "si")
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({ArticuloService.class, CreateArticuloUseCaseImpl.class, UpdateArticuloUseCaseImpl.class, DeleteArticuloUseCaseImpl.class,
        GetAllArticulosUseCaseImpl.class, GetArticuloByIdUseCaseImpl.class, GetNewArticuloUseCaseImpl.class,
        GetPaginatedArticulosUseCaseImpl.class, SearchArticulosUseCaseImpl.class, JpaArticuloRepositoryAdapter.class,
        ArticuloMapper.class, CuentaMapper.class, UbicacionArticuloService.class, SaveUbicacionArticuloUseCaseImpl.class,
        GetAllUbicacionArticulosUseCaseImpl.class, GetUbicacionArticuloUseCaseImpl.class,
        GetUbicacionArticulosByArticuloUseCaseImpl.class, JpaUbicacionArticuloRepositoryAdapter.class,
        UbicacionArticuloMapper.class, UbicacionMapper.class, GastosConcurrenciaDevE2E.Auditoria.class})
class GastosConcurrenciaDevE2E {

    static final String MARCA = DevDbReservas.MARCA + " conc";
    static final long ID_EDICION = 999_011L, ID_ASIGNACION = 999_012L, ID_PAR_NUEVO = 999_013L,
            ID_ESPERA = 999_014L, ID_INTERBLOQUEO = 999_015L, ID_SIN_CAMBIO = 999_016L;
    /** Espera de bloqueo de las conexiones de la app en esta prueba (en dev es 50 s): fuerza un 1205 real rápido. */
    static final int ESPERA_SEGUNDOS = 5;

    @TestConfiguration
    @EnableJpaAuditing
    static class Auditoria {
    }

    @DynamicPropertySource
    static void devDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&connectTimeout=5000"
                .formatted(System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium")));
        registry.add("spring.datasource.username", () -> System.getenv("IT_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("IT_DB_PASSWORD"));
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "3");
        registry.add("spring.datasource.hikari.connection-init-sql", () -> "SET SESSION innodb_lock_wait_timeout = " + ESPERA_SEGUNDOS);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.session_factory.statement_inspector", SoloTablasDelFlujo.class::getName);
    }

    /** Hibernate solo puede escribir en las dos tablas del flujo (los ids los controla la prueba). */
    public static class SoloTablasDelFlujo implements StatementInspector {

        private static final Set<String> PERMITIDAS = Set.of("articulos", "ubicacion_articulo");
        private static final Pattern ESCRITURA = Pattern.compile(
                "^\\s*(?:insert\\s+into|update|delete\\s+from)\\s+`?(\\w+)`?", Pattern.CASE_INSENSITIVE);

        @Override
        public String inspect(String sql) {
            var m = ESCRITURA.matcher(sql);
            if (m.find() && !PERMITIDAS.contains(m.group(1).toLowerCase(Locale.ROOT))) {
                throw new IllegalStateException("La prueba intentó escribir fuera del flujo: " + m.group(1));
            }
            return sql;
        }
    }

    static DevDbReservas dev;

    @Autowired CreateArticuloUseCase crear;
    @Autowired UpdateArticuloUseCase editar;
    @Autowired UbicacionArticuloService asignaciones;
    @Autowired ArticuloService articulos;

    Integer ubicacion;
    BigDecimal cuentaA, cuentaB;

    @BeforeAll
    static void reservarYFotografiar() throws SQLException {
        dev = DevDbReservas.abrir();
        // Falla (no se saltea): un salteo silencioso parecería verde. Si quedaron restos de una corrida cortada,
        // revisarlos y borrarlos a mano por su marca E2E-405-xxxxxxxx.
        assertThat(dev.rangoLibre()).as("hay filas con ids reservados del E2E en dev (otra corrida o restos): no se toca nada").isTrue();
        dev.fotografiar();
    }

    @BeforeEach
    void datosDeReferencia() throws SQLException {
        ubicacion = (int) dev.numero("SELECT MIN(ubicacion_id) FROM ubicacion");
        try (var st = dev.db.createStatement(); var rs = st.executeQuery("SELECT pla_cuenta FROM plancta ORDER BY pla_cuenta LIMIT 2")) {
            rs.next(); cuentaA = rs.getBigDecimal(1);
            rs.next(); cuentaB = rs.getBigDecimal(1);
        }
    }

    @AfterEach
    void limpiarLoPropio() throws SQLException {
        dev.barrer();
    }

    @AfterAll
    static void restaurarYVerificar() throws SQLException {
        if (dev == null) return;
        try {
            if (dev.fotografiado()) dev.restaurarYVerificar();
        } finally {
            dev.close();
        }
    }

    @Test
    void edicionesConcurrentes_noDeshacenElCambioConfirmadoPorOtro() throws Exception {
        crear.createArticulo(gasto(ID_EDICION));

        // A cambia habilitado; B (el caso de uso) cambia solo el nombre mientras A tiene la fila
        var resultado = conOtroEscritorBloqueando(
                "UPDATE articulos SET Art_Habilitado = 0 WHERE Art_ID = " + ID_EDICION,
                () -> editar.updateArticulo(ID_EDICION, Articulo.builder().nombre(MARCA + " B").build()));

        assertThat(resultado.getNombre()).isEqualTo(MARCA + " B");
        assertThat(dev.numero("SELECT Art_Habilitado + 0 FROM articulos WHERE Art_ID = ?", ID_EDICION))
                .as("el habilitado = 0 que confirmó A no se pierde").isZero();
        assertThat(dev.numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = ? AND Art_Nombre = ?", ID_EDICION, MARCA + " B"))
                .isEqualTo(1);
    }

    @Test
    void asignacionConcurrente_guardaLaCuentaPedidaAunqueOtroLaHayaCambiado() throws Exception {
        crear.createArticulo(gasto(ID_ASIGNACION));
        asignaciones.save(asignacion(ID_ASIGNACION, cuentaA));

        // A cambia la cuenta a B; el caso de uso pide A: sin datos frescos no vería nada que cambiar
        var resultado = conOtroEscritorBloqueando(
                "UPDATE ubicacion_articulo SET cuenta_contable = " + cuentaB + " WHERE articulo_id = " + ID_ASIGNACION,
                () -> asignaciones.save(asignacion(ID_ASIGNACION, cuentaA)));

        assertThat(resultado.getNumeroCuenta()).isEqualByComparingTo(cuentaA);
        assertThat(cuentas(ID_ASIGNACION)).as("la base guarda lo que respondió el servicio").containsExactly(cuentaA);
    }

    @Test
    void asignacionConcurrenteConLaMismaCuenta_respondeLaCuentaVigente() throws Exception {
        crear.createArticulo(gasto(ID_SIN_CAMBIO));
        asignaciones.save(asignacion(ID_SIN_CAMBIO, cuentaA));

        // A cambia la cuenta a B y el caso de uso también pide B: no hay UPDATE propio que mostrar
        var resultado = conOtroEscritorBloqueando(
                "UPDATE ubicacion_articulo SET cuenta_contable = " + cuentaB + " WHERE articulo_id = " + ID_SIN_CAMBIO,
                () -> asignaciones.save(asignacion(ID_SIN_CAMBIO, cuentaB)));

        assertThat(resultado.getNumeroCuenta()).isEqualByComparingTo(cuentaB);
        assertThat(resultado.getCuenta().getNumeroCuenta()).as("la cuenta cargada es la vigente, no la de la foto").isEqualByComparingTo(cuentaB);
        assertThat(cuentas(ID_SIN_CAMBIO)).containsExactly(cuentaB);
    }

    @Test
    void insercionConcurrenteDelPar_seReintentaEnOtraTransaccionYQuedaUnaFila() throws Exception {
        crear.createArticulo(gasto(ID_PAR_NUEVO));

        // A inserta el mismo par sin confirmar: el INSERT del caso de uso espera y, al confirmar A, choca con 1062
        var resultado = conOtroEscritorBloqueando(
                "INSERT INTO ubicacion_articulo (ubicacion_id, articulo_id, cuenta_contable, created) VALUES ("
                        + ubicacion + ", " + ID_PAR_NUEVO + ", " + cuentaA + ", NOW())",
                () -> asignaciones.save(asignacion(ID_PAR_NUEVO, cuentaB)));

        assertThat(resultado.getNumeroCuenta()).isEqualByComparingTo(cuentaB);
        assertThat(cuentas(ID_PAR_NUEVO)).as("una sola fila, con la cuenta del reintento").containsExactly(cuentaB);
    }

    @Test
    void esperaDeBloqueoVencida_409SinReintentoYSinCambios() throws Exception {
        crear.createArticulo(gasto(ID_ESPERA));
        asignaciones.save(asignacion(ID_ESPERA, cuentaA));

        try (var otro = DevDbReservas.abrir()) {
            Connection c = otro.db;
            c.setAutoCommit(false);
            try {
                // Otro tiene el artículo y el vínculo tomados más que la espera de la app
                try (var st = c.createStatement()) {
                    st.executeUpdate("UPDATE articulos SET Art_Habilitado = 0 WHERE Art_ID = " + ID_ESPERA);
                    st.executeUpdate("UPDATE ubicacion_articulo SET cuenta_contable = " + cuentaB + " WHERE articulo_id = " + ID_ESPERA);
                }
                var inicio = System.nanoTime();
                var edicion = org.assertj.core.api.Assertions.catchThrowable(
                        () -> articulos.updateArticulo(ID_ESPERA, Articulo.builder().nombre(MARCA + " no").build()));
                var segundos = (System.nanoTime() - inicio) / 1e9;
                var asignacion = org.assertj.core.api.Assertions.catchThrowable(() -> asignaciones.save(asignacion(ID_ESPERA, cuentaB)));

                assertThat(edicion).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
                    assertThat(ex.getMotivo()).isEqualTo(ArticuloConflictException.Motivo.BLOQUEADO);
                    assertThat(ex.isReintentable()).isFalse();
                });
                assertThat(segundos).as("una sola espera: no se reintentó").isLessThan(ESPERA_SEGUNDOS * 2);
                assertThat(asignacion).isInstanceOfSatisfying(UbicacionArticuloConflictException.class,
                        ex -> assertThat(ex.isBloqueado()).isTrue());
            } finally {
                c.rollback();
            }
        }
        assertThat(dev.numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = ? AND Art_Nombre = ? AND Art_Habilitado = 1", ID_ESPERA, MARCA + " A"))
                .as("ni la app ni el otro dejaron cambios").isEqualTo(1);
        assertThat(cuentas(ID_ESPERA)).containsExactly(cuentaA);
    }

    @Test
    void interbloqueoRealEnLaBaja_terminaEn409SinBorrarNiError500() throws Exception {
        crear.createArticulo(gasto(ID_INTERBLOQUEO));
        asignaciones.save(asignacion(ID_INTERBLOQUEO, cuentaA));

        var executor = Executors.newSingleThreadExecutor();
        String victima;
        Throwable resultado;
        try (var otro = DevDbReservas.abrir()) {
            Connection c = otro.db;
            c.setAutoCommit(false);
            try {
                // Otro toma el vínculo; la baja toma el artículo y espera el vínculo (chequeo de la FK);
                // después otro pide el artículo: ciclo de esperas, MySQL elige una víctima
                try (var st = c.createStatement()) {
                    st.executeUpdate("UPDATE ubicacion_articulo SET cuenta_contable = " + cuentaB + " WHERE articulo_id = " + ID_INTERBLOQUEO);
                }
                var futuro = executor.submit(() -> {
                    articulos.deleteArticulo(ID_INTERBLOQUEO);
                    return null;
                });
                esperarQueEspere(otro.numero("SELECT CONNECTION_ID()"));
                try (var st = c.createStatement()) {
                    st.executeUpdate("UPDATE articulos SET Art_Nombre = '" + MARCA + " otro' WHERE Art_ID = " + ID_INTERBLOQUEO);
                    victima = "la baja (se reintentó)";
                    c.commit();
                } catch (SQLException ex) {
                    assertThat(ex.getErrorCode()).as("el otro escritor solo puede caer por interbloqueo").isEqualTo(1213);
                    victima = "el otro escritor";
                }
                resultado = org.assertj.core.api.Assertions.catchThrowable(() -> futuro.get(30, TimeUnit.SECONDS));
            } finally {
                if (!c.getAutoCommit()) c.rollback();
            }
        } finally {
            terminar(executor);
        }
        // InnoDB sacrifica la transacción más liviana: la baja todavía no modificó filas, el otro ya modificó una
        assertThat(victima).as("víctima del interbloqueo: así se ejerce el reintento de la baja").isEqualTo("la baja (se reintentó)");
        assertThat(resultado).as("la baja termina en el 409 del vínculo, nunca en un 500")
                .hasCauseInstanceOf(ArticuloConflictException.class)
                .cause().satisfies(ex -> assertThat(((ArticuloConflictException) ex).getMotivo()).isEqualTo(ArticuloConflictException.Motivo.REFERENCIADO));
        assertThat(dev.numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = ?", ID_INTERBLOQUEO)).isEqualTo(1);
    }

    /**
     * Otra conexión ejecuta {@code sqlOtro} sin confirmar; {@code operacion} corre en otro hilo hasta quedar bloqueada
     * por esa conexión; entonces la otra confirma y se devuelve el resultado de la operación.
     */
    private <T> T conOtroEscritorBloqueando(String sqlOtro, Callable<T> operacion) throws Exception {
        var executor = Executors.newSingleThreadExecutor();
        try (var otro = DevDbReservas.abrir()) {
            Connection c = otro.db;
            c.setAutoCommit(false);
            try {
                try (var st = c.createStatement()) {
                    st.executeUpdate(sqlOtro);
                }
                long hiloOtro = otro.numero("SELECT CONNECTION_ID()");
                var futuro = executor.submit(operacion);
                esperarQueEspere(hiloOtro);
                c.commit();
                return futuro.get(30, TimeUnit.SECONDS);
            } finally {
                if (!c.getAutoCommit()) c.rollback(); // sin efecto si ya confirmó
            }
        } finally {
            terminar(executor);
        }
    }

    /** Ninguna escritura de la app puede llegar después del barrido: se espera al hilo y, si no termina, falla. */
    private static void terminar(java.util.concurrent.ExecutorService executor) throws InterruptedException {
        executor.shutdownNow();
        assertThat(executor.awaitTermination(ESPERA_SEGUNDOS * 3L, TimeUnit.SECONDS))
                .as("la operación de la app siguió corriendo después de la prueba").isTrue();
    }

    /** Espera a que alguna transacción quede bloqueada por la conexión {@code hiloBloqueante}. */
    private void esperarQueEspere(long hiloBloqueante) throws Exception {
        for (int i = 0; i < 150; i++) {
            if (dev.numero("""
                    SELECT COUNT(*) FROM information_schema.INNODB_LOCK_WAITS w
                    JOIN information_schema.INNODB_TRX b ON b.trx_id = w.blocking_trx_id
                    WHERE b.trx_mysql_thread_id = ?""", hiloBloqueante) > 0) {
                return;
            }
            Thread.sleep(100);
        }
        fail("la operación nunca quedó esperando el bloqueo de la otra conexión");
    }

    private List<BigDecimal> cuentas(long articuloId) throws SQLException {
        try (var st = dev.db.prepareStatement("SELECT cuenta_contable FROM ubicacion_articulo WHERE ubicacion_id = ? AND articulo_id = ?")) {
            st.setInt(1, ubicacion);
            st.setLong(2, articuloId);
            try (var rs = st.executeQuery()) {
                var r = new java.util.ArrayList<BigDecimal>();
                while (rs.next()) r.add(rs.getBigDecimal(1));
                return r;
            }
        }
    }

    private static Articulo gasto(long id) {
        return Articulo.builder().articuloId(id).nombre(MARCA + " A").descripcion("E2E").unidad("UN")
                .tipo("gasto").directo((byte) 0).habilitado((byte) 1).build();
    }

    private UbicacionArticulo asignacion(long articuloId, BigDecimal cuenta) {
        return UbicacionArticulo.builder().ubicacionId(ubicacion).articuloId(articuloId).numeroCuenta(cuenta).build();
    }
}
