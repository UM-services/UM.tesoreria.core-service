package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.application.usecases.*;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.adapter.CuentaHistorialAdapter;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service.EscrituraHistorialService;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.adapter.JpaEscrituraHistorialRepositoryAdapter;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper.EscrituraHistorialMapper;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization.JacksonEscrituraValorSerializer;
import um.tesoreria.core.service.view.CuentaSearchService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Prueba los casos de uso con transacciones, JPA e historial reales; sin transacción envolvente del test. */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@org.springframework.boot.autoconfigure.ImportAutoConfiguration(
        org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CreateCuentaUseCaseImpl.class, UpdateCuentaUseCaseImpl.class, DeleteCuentaUseCaseImpl.class,
        RecalculaGradosUseCaseImpl.class, JpaCuentaRepositoryAdapter.class, CuentaMapper.class,
        CuentaHistorialAdapter.class, EscrituraHistorialService.class, JpaEscrituraHistorialRepositoryAdapter.class,
        EscrituraHistorialMapper.class, JacksonEscrituraValorSerializer.class, CuentaPersistenceTest.Auditoria.class})
public class CuentaPersistenceTest {
    @TestConfiguration
    @EnableJpaAuditing
    public static class Auditoria { }

    @Autowired CreateCuentaUseCaseImpl crear;
    @Autowired UpdateCuentaUseCaseImpl editar;
    @Autowired DeleteCuentaUseCaseImpl borrar;
    @Autowired RecalculaGradosUseCaseImpl recalcular;
    @Autowired JpaCuentaRepositoryAdapter repository;
    @Autowired JdbcTemplate jdbc;
    @Autowired jakarta.persistence.EntityManager entityManager;
    @Autowired org.springframework.transaction.PlatformTransactionManager transactionManager;
    @Autowired javax.sql.DataSource dataSource;
    @MockitoBean CuentaSearchService search;
    @MockitoSpyBean CuentaHistorialAdapter historial;
    @MockitoSpyBean JpaCuentaRepository jpaRepository;

    @BeforeEach
    void limpiar() {
        jdbc.update("DELETE FROM movcon");
        jdbc.update("DELETE FROM gestion_escritura_historial");
        jdbc.update("DELETE FROM plancta");
    }

    static Cuenta cuenta(String numero) {
        return Cuenta.builder().numeroCuenta(new BigDecimal(numero)).nombre("Prueba 407")
                .visible((byte) 1).integradora((byte) 0).build();
    }

    @Test
    void altaIntegradoraAtomicaYDuplicadoNoPisa() {
        crear.createCuenta(cuenta("10000000000"));
        crear.createCuenta(cuenta("11000000000"));
        assertThat(jdbc.queryForObject("SELECT pla_integra FROM plancta WHERE pla_cuenta=10000000000", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(3);
        assertThatThrownBy(() -> crear.createCuenta(cuenta("11000000000")))
                .isInstanceOf(CuentaConflictoException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(3);
    }

    @Test
    void fallaSegundaIntegradoraRevierteAltaAmbasIntegradorasEHistorial() {
        crear.createCuenta(cuenta("10000000000"));
        crear.createCuenta(cuenta("11000000000"));
        jdbc.update("UPDATE plancta SET pla_integra=0");
        int eventos = jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class);
        AtomicInteger ediciones = new AtomicInteger();
        doAnswer(i -> {
            if (ediciones.incrementAndGet() == 2) throw new IllegalStateException("Falla segunda integradora");
            return i.callRealMethod();
        }).when(historial).registrarEdicion(any(), any());
        assertThatThrownBy(() -> crear.createCuenta(cuenta("11010000000"))).isInstanceOf(IllegalStateException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT SUM(pla_integra) FROM plancta", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(eventos);
    }

    @Test
    void edicionParcialConservaDatosSedeCeroSeLeeYCreatedNoCambia() {
        crear.createCuenta(cuenta("10000000000"));
        var antes = jdbc.queryForMap("SELECT * FROM plancta");
        editar.updateCuenta(new BigDecimal("10000000000"), Cuenta.builder().nombre("Editado").geograficaId(0).build());
        var despues = jdbc.queryForMap("SELECT * FROM plancta");
        assertThat(despues.get("created")).isEqualTo(antes.get("created"));
        assertThat(despues.get("id")).isEqualTo(antes.get("id"));
        assertThat(repository.findById(new BigDecimal("10000000000")).orElseThrow().getGeograficaId()).isNull();
        assertThat(despues.get("pla_grado")).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT visible FROM plancta", Integer.class)).isEqualTo(1);
    }

    @Test
    void bajaConHijasRechazaYLibreBorraConHistorial() {
        crear.createCuenta(cuenta("10000000000"));
        crear.createCuenta(cuenta("11000000000"));
        assertThatThrownBy(() -> borrar.deleteCuenta(new BigDecimal("10000000000")))
                .isInstanceOf(CuentaConflictoException.class);
        borrar.deleteCuenta(new BigDecimal("11000000000"));
        borrar.deleteCuenta(new BigDecimal("11000000000"));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(4);
    }

    void movimiento(String numero) {
        jdbc.update("INSERT INTO movcon (mco_fecha,mco_nrocomp,mco_item,mco_pla_cuenta,mco_debita,"
                + "mco_concepto,mco_importe,mco_nroanulado,version,apertura,created) "
                + "VALUES (CURRENT_TIMESTAMP,407,1,?,0,'IT407',1,0,0,0,CURRENT_TIMESTAMP)", new BigDecimal(numero));
    }

    @Test
    void movimientosPropiosBloqueanBajaYAltaDeHijaSinCambiarHistorial() {
        crear.createCuenta(cuenta("10000000000"));
        movimiento("10000000000");
        assertThatThrownBy(() -> borrar.deleteCuenta(new BigDecimal("10000000000")))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("registraciones");
        assertThatThrownBy(() -> crear.createCuenta(cuenta("11000000000")))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("registraciones");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(1);
    }

    @Test
    void recalculoNoInsertaYNoReescribeFilasCorrectas() {
        crear.createCuenta(cuenta("10000000000"));
        var antes = jdbc.queryForMap("SELECT * FROM plancta");
        assertThat(recalcular.recalculaGrados()).isEqualTo("Ok");
        assertThat(jdbc.queryForMap("SELECT * FROM plancta")).isEqualTo(antes);
        borrar.deleteCuenta(new BigDecimal("10000000000"));
        recalcular.recalculaGrados();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isZero();
    }

    @Test
    void recalculoNoSeCortaPorUnNumeroLegadoInvalido() {
        crear.createCuenta(cuenta("10000000000"));
        jdbc.update("UPDATE plancta SET pla_grado=0 WHERE pla_cuenta=10000000000");
        jdbc.update("INSERT INTO plancta (pla_cuenta, pla_nombre, pla_integra, pla_grado, visible, created, updated) VALUES (11000010001, 'Legado', 0, 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        assertThat(recalcular.recalculaGrados()).isEqualTo("Ok");
        assertThat(jdbc.queryForObject("SELECT pla_grado FROM plancta WHERE pla_cuenta=10000000000", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT pla_grado FROM plancta WHERE pla_cuenta=11000010001", Integer.class)).isZero();
    }

    @Test
    void borradoConcurrenteDuranteRecalculoRevierteSinReinsertar() throws Exception {
        // Las tablas TEMPORARY de MySQL pertenecen a una sola conexión; este caso usa dos conexiones H2.
        try (var conexion = dataSource.getConnection()) {
            org.junit.jupiter.api.Assumptions.assumeTrue(conexion.getMetaData().getDatabaseProductName().equals("H2"));
        }
        crear.createCuenta(cuenta("10000000000"));
        jdbc.update("UPDATE plancta SET pla_grado=0");
        var transaction = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
        transaction.setIsolationLevel(org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ);
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            entityManager.find(um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity.class,
                    new BigDecimal("10000000000"));
            try (var conexion = dataSource.getConnection(); var st = conexion.createStatement()) {
                st.executeUpdate("DELETE FROM plancta WHERE pla_cuenta=10000000000");
            } catch (java.sql.SQLException ex) {
                throw new IllegalStateException(ex);
            }
            recalcular.recalculaGrados();
        })).isInstanceOf(org.springframework.dao.ConcurrencyFailureException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isZero();
    }
    @Test
    void limpiezaBloqueaNombreHastaCommitYNoPierdeEdicionAuditada() throws Exception {
        try (var conexion = dataSource.getConnection()) {
            org.junit.jupiter.api.Assumptions.assumeTrue(conexion.getMetaData().getDatabaseProductName().equals("H2"));
        }
        crear.createCuenta(cuenta("10000000000"));
        jdbc.update("UPDATE plancta SET pla_nombre=?", "Anterior\r\n");
        var leido = new java.util.concurrent.CountDownLatch(1);
        var continuar = new java.util.concurrent.CountDownLatch(1);
        org.mockito.stubbing.Answer<Object> pausarLectura = invocation -> {
            Object resultado = mockingDetails(jpaRepository).getMockCreationSettings().getDefaultAnswer().answer(invocation);
            leido.countDown();
            if (!continuar.await(10, java.util.concurrent.TimeUnit.SECONDS))
                throw new IllegalStateException("No se liberó la limpieza");
            return resultado;
        };
        doAnswer(pausarLectura).when(jpaRepository).findAll();
        doAnswer(pausarLectura).when(jpaRepository).findAllByNombreContainingOrderByNumeroCuenta("\r\n");
        try (var executor = java.util.concurrent.Executors.newSingleThreadExecutor()) {
            var limpieza = executor.submit(() -> repository.limpiarNombres());
            try {
                assertThat(leido.await(10, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
                try (var conexion = dataSource.getConnection(); var st = conexion.createStatement()) {
                    st.execute("SET LOCK_TIMEOUT 250");
                    assertThatThrownBy(() -> st.executeUpdate("UPDATE plancta SET pla_nombre='Nuevo' WHERE pla_cuenta=10000000000"))
                            .isInstanceOf(java.sql.SQLException.class);
                }
            } finally {
                continuar.countDown();
                limpieza.get(10, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
        assertThat(repository.findById(new BigDecimal("10000000000")).orElseThrow().getNombre()).isEqualTo("Anterior");
        editar.updateCuenta(new BigDecimal("10000000000"), Cuenta.builder().nombre("Nuevo").build());
        assertThat(repository.findById(new BigDecimal("10000000000")).orElseThrow().getNombre()).isEqualTo("Nuevo");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(2);
    }

    @Test
    void sumasSaldosConservaSalidaTrasRecalcularGrados(@org.junit.jupiter.api.io.TempDir java.nio.file.Path directorio) throws Exception {
        for (String numero : java.util.List.of("10000000000", "11000000000", "11010000000", "11010100000",
                "11010100001", "11010100002", "11010100003")) {
            crear.createCuenta(cuenta(numero));
        }
        jdbc.update("UPDATE plancta SET pla_nombre=? WHERE pla_cuenta=11010100001", "Deudora\r\n");
        jdbc.update("UPDATE plancta SET pla_nombre='Acreedora' WHERE pla_cuenta=11010100002");
        jdbc.update("UPDATE plancta SET pla_nombre='Sin movimientos' WHERE pla_cuenta=11010100003");
        var cuentas = new um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService(
                crear, editar, borrar, null, null, null, null, recalcular, null, repository);
        var contabilidad = spy(new um.tesoreria.core.service.facade.ContabilidadService(
                null, cuentas, null, null, null, null, null, null));
        var desde = java.time.OffsetDateTime.parse("2026-01-01T00:00:00Z");
        var hasta = java.time.OffsetDateTime.parse("2026-01-31T00:00:00Z");
        var ejercicio = mock(um.tesoreria.core.kotlin.model.Ejercicio.class);
        doReturn(ejercicio).when(contabilidad).verifyEjercicio(desde, hasta);
        doAnswer(i -> switch (((BigDecimal) i.getArgument(0)).remainder(BigDecimal.TEN).intValueExact()) {
            case 1 -> java.util.List.of(new BigDecimal("100.50"), new BigDecimal("20.25"));
            case 2 -> java.util.List.of(BigDecimal.ZERO, new BigDecimal("80"));
            default -> java.util.List.of(BigDecimal.ZERO, BigDecimal.ZERO);
        }).when(contabilidad).saldoInicial(any(), eq(ejercicio), eq(desde));
        doAnswer(i -> switch (((BigDecimal) i.getArgument(0)).remainder(BigDecimal.TEN).intValueExact()) {
            case 1 -> java.util.List.of(new BigDecimal("30.25"), new BigDecimal("10"));
            case 2 -> java.util.List.of(new BigDecimal("10"), new BigDecimal("40"));
            default -> java.util.List.of(BigDecimal.ZERO, BigDecimal.ZERO);
        }).when(contabilidad).saldoPeriodo(any(), eq(desde), eq(hasta), eq((byte) 0));
        var balance = new um.tesoreria.core.service.facade.BalanceService(
                new org.springframework.mock.env.MockEnvironment().withProperty("path.files", directorio + java.io.File.separator),
                cuentas, contabilidad, null, null, null, null, null, null, null);
        int eventos = jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class);
        var esperado = java.util.List.of(
                java.util.List.of("Desde 01-01-2026"), java.util.List.of("Hasta 31-01-2026"),
                java.util.List.of("#Cuenta", "Cuenta", "S.Inicial Débitos", "S.Inicial Créditos", "Débitos", "Créditos", "Saldo Deudor", "Saldo Acreedor"),
                java.util.List.of(11010100001d, "Deudora", 100.50d, 20.25d, 30.25d, 10d, 100.50d, 0d),
                java.util.List.of(11010100002d, "Acreedora", 0d, 80d, 10d, 40d, 0d, 110d),
                java.util.List.of(11010100003d, "Sin movimientos", 0d, 0d, 0d, 0d, 0d, 0d));
        var primera = leerBalance(balance.makeSumasSaldos(desde, hasta));
        assertThat(primera).isEqualTo(esperado);
        jdbc.update("UPDATE plancta SET pla_grado=0,pla_grado1=0,pla_grado2=0,pla_grado3=0,pla_grado4=0");
        assertThat(leerBalance(balance.makeSumasSaldos(desde, hasta))).isEqualTo(primera);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gestion_escritura_historial", Integer.class)).isEqualTo(eventos);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM plancta", Integer.class)).isEqualTo(7);
    }

    private static java.util.List<java.util.List<Object>> leerBalance(String archivo) throws Exception {
        try (var entrada = java.nio.file.Files.newInputStream(java.nio.file.Path.of(archivo));
             var book = new org.apache.poi.xssf.usermodel.XSSFWorkbook(entrada)) {
            assertThat(book.getNumberOfSheets()).isEqualTo(1);
            var sheet = book.getSheet("Balance");
            assertThat(sheet).isNotNull();
            var filas = new java.util.ArrayList<java.util.List<Object>>();
            for (var row : sheet) {
                var valores = new java.util.ArrayList<Object>();
                for (var cell : row) valores.add(cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC
                        ? cell.getNumericCellValue() : cell.getStringCellValue());
                filas.add(valores);
            }
            return filas;
        }
    }

}
