package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository.JpaAsientoRepository;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository.JpaCuentaMovimientoRepository;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.EjercicioFechas;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Bordes del rango por fecha de las dos consultas derivadas de la baja segura (H2, sin depender de dev). */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@Import(ConsultasDeUsoPersistenceTest.Auditoria.class)
class ConsultasDeUsoPersistenceTest {
    @TestConfiguration
    @EnableJpaAuditing
    static class Auditoria { }

    @Autowired JpaAsientoRepository asientos;
    @Autowired JpaCuentaMovimientoRepository movimientos;
    @Autowired JdbcTemplate jdbc;

    final OffsetDateTime desde = EjercicioFechas.aInicioDeDia(LocalDate.parse("2027-03-01"));
    final OffsetDateTime hasta = EjercicioFechas.aFinDeDia(LocalDate.parse("2028-02-29"));

    @BeforeEach
    void limpiar() {
        jdbc.update("DELETE FROM movcon");
        jdbc.update("DELETE FROM asiento");
    }

    static OffsetDateTime utc(String fechaHora) {
        return OffsetDateTime.parse(fechaHora.replace(' ', 'T') + "Z");
    }

    void asiento(String fechaHora, int orden) {
        asientos.saveAndFlush(AsientoEntity.builder().fecha(utc(fechaHora)).orden(orden).vinculo("prueba 408").build());
    }

    void movimiento(String fechaHora, int item) {
        movimientos.saveAndFlush(CuentaMovimientoEntity.builder().fechaContable(utc(fechaHora)).ordenContable(1).item(item).build());
    }

    @Test
    void asientoEnLosDosBordesDelPeriodoCuentaYUnoFueraNo() {
        assertThat(asientos.existsByFechaBetween(desde, hasta)).isFalse();
        asiento("2027-02-28 23:59:59", 1);
        asiento("2028-03-01 00:00:00", 2);
        assertThat(asientos.existsByFechaBetween(desde, hasta)).as("justo fuera").isFalse();
        asiento("2027-03-01 00:00:00", 3);
        assertThat(asientos.existsByFechaBetween(desde, hasta)).as("primer instante").isTrue();
        asientos.deleteAll(asientos.findAll().stream().filter(x -> x.getOrden() == 3).toList());
        asientos.flush();
        asiento("2028-02-29 23:59:59", 4);
        assertThat(asientos.existsByFechaBetween(desde, hasta)).as("último instante").isTrue();
    }

    @Test
    void movimientoEnLosDosBordesDelPeriodoCuentaYUnoFueraNo() {
        assertThat(movimientos.existsByFechaContableBetween(desde, hasta)).isFalse();
        movimiento("2027-02-28 23:59:59", 1);
        movimiento("2028-03-01 00:00:00", 2);
        assertThat(movimientos.existsByFechaContableBetween(desde, hasta)).as("justo fuera").isFalse();
        movimiento("2027-03-01 00:00:00", 3);
        assertThat(movimientos.existsByFechaContableBetween(desde, hasta)).as("primer instante").isTrue();
        movimientos.deleteAll(movimientos.findAll().stream().filter(x -> x.getItem() == 3).toList());
        movimientos.flush();
        movimiento("2028-02-29 23:59:59", 4);
        assertThat(movimientos.existsByFechaContableBetween(desde, hasta)).as("último instante").isTrue();
    }
}
