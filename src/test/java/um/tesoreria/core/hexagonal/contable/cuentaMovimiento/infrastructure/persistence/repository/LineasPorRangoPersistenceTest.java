package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository.JpaProveedorRepository;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository.ProveedorNombreProjection;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Lectura por rango de `movcon` con proyección y nombres de proveedor por id (H2). */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF",
        "spring.jpa.properties.hibernate.generate_statistics=true"})
@Import(LineasPorRangoPersistenceTest.Auditoria.class)
class LineasPorRangoPersistenceTest {
    @TestConfiguration
    @EnableJpaAuditing
    static class Auditoria { }

    static final Sort ORDEN = Sort.by("fechaContable", "ordenContable", "item", "cuentaMovimientoId").ascending();

    @Autowired JpaCuentaMovimientoRepository movimientos;
    @Autowired JpaProveedorRepository proveedores;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager entityManager;
    @Autowired EntityManagerFactory entityManagerFactory;

    Integer proveedorId;

    @BeforeEach
    void cargar() {
        // El esquema de prueba crea claves foráneas por las relaciones de la entidad; acá sólo importan las columnas.
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbc.update("DELETE FROM movcon");
        proveedorId = proveedores.saveAndFlush(ProveedorEntity.builder().razonSocial("Proveedor Uno").cuit("30-71111111-1").build())
                .getProveedorId();
        movimiento("2025-02-28T23:59:59Z", 9, 1);
        movimiento("2025-03-02T00:00:00Z", 1, 2);
        movimiento("2025-03-01T00:00:00Z", 2, 1);
        movimiento("2025-03-01T00:00:00Z", 1, 2);
        movimiento("2025-03-01T00:00:00Z", 1, 1);
        movimiento("2025-03-03T00:00:00Z", 9, 1);
        entityManager.clear();
    }

    @org.junit.jupiter.api.AfterEach
    void restaurar() {
        jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    void movimiento(String fecha, int orden, int item) {
        movimientos.saveAndFlush(CuentaMovimientoEntity.builder()
                .fechaContable(OffsetDateTime.parse(fecha)).ordenContable(orden).item(item)
                .numeroCuenta(new BigDecimal("11101010001")).debita((byte) 1).importe(new BigDecimal("10.50"))
                .concepto("c").comprobanteId(3).proveedorId(proveedorId).build());
    }

    Statistics estadisticas() {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        return statistics;
    }

    @Test
    void rangoSemiabiertoOrdenadoEnUnaSolaSentencia() {
        Statistics statistics = estadisticas();

        List<LineaMovimientoProjection> lineas = movimientos.findAllByFechaContableGreaterThanEqualAndFechaContableLessThan(
                OffsetDateTime.parse("2025-03-01T00:00:00Z"), OffsetDateTime.parse("2025-03-03T00:00:00Z"), ORDEN,
                Limit.unlimited());

        assertThat(lineas).extracting(l -> l.getFechaContable().toLocalDate().getDayOfMonth(),
                        LineaMovimientoProjection::getOrdenContable, LineaMovimientoProjection::getItem)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1, 1, 1),
                        org.assertj.core.groups.Tuple.tuple(1, 1, 2),
                        org.assertj.core.groups.Tuple.tuple(1, 2, 1),
                        org.assertj.core.groups.Tuple.tuple(2, 1, 2));
        LineaMovimientoProjection primera = lineas.getFirst();
        assertThat(primera.getImporte()).isEqualByComparingTo("10.50");
        assertThat(primera.getProveedorId()).isEqualTo(proveedorId);
        assertThat(primera.getComprobanteId()).isEqualTo(3);
        assertThat(primera.getDebita()).isEqualTo((byte) 1);
        assertThat(statistics.getPrepareStatementCount()).as("sin cargar relaciones").isEqualTo(1);
    }

    @Test
    void elLimiteCortaLaLecturaEnElOrdenPedido() {
        List<LineaMovimientoProjection> lineas = movimientos.findAllByFechaContableGreaterThanEqualAndFechaContableLessThan(
                OffsetDateTime.parse("2025-03-01T00:00:00Z"), OffsetDateTime.parse("2025-03-03T00:00:00Z"), ORDEN,
                Limit.of(3));

        assertThat(lineas).extracting(LineaMovimientoProjection::getOrdenContable, LineaMovimientoProjection::getItem)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1, 1),
                        org.assertj.core.groups.Tuple.tuple(1, 2),
                        org.assertj.core.groups.Tuple.tuple(2, 1));
    }

    @Test
    void cuentaConLosMismosBordes() {
        assertThat(movimientos.countByFechaContableGreaterThanEqualAndFechaContableLessThan(
                OffsetDateTime.parse("2025-03-01T00:00:00Z"), OffsetDateTime.parse("2025-03-03T00:00:00Z")))
                .isEqualTo(4);
    }

    @Test
    void nombresDeProveedorEnUnaSolaSentencia() {
        Statistics statistics = estadisticas();

        List<ProveedorNombreProjection> nombres = proveedores.findAllByProveedorIdIn(List.of(proveedorId, -1));

        assertThat(nombres).extracting(ProveedorNombreProjection::getRazonSocial).containsExactly("Proveedor Uno");
        assertThat(nombres).extracting(ProveedorNombreProjection::getCuit).containsExactly("30-71111111-1");
        assertThat(statistics.getPrepareStatementCount()).as("sin cargar la cuenta").isEqualTo(1);
    }
}
