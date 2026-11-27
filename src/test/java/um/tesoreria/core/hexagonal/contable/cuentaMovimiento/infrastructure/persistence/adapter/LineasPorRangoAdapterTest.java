package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.adapter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.mapper.CuentaMovimientoMapper;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository.JpaCuentaMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

/** El adaptador ordena las líneas y copia cada columna a su campo (H2). */
@DataJpaTest(properties = {"spring.jpa.show-sql=false", "logging.level.org.hibernate.SQL=OFF"})
@Import({LineasPorRangoAdapterTest.Auditoria.class, JpaCuentaMovimientoRepositoryAdapter.class, CuentaMovimientoMapper.class})
class LineasPorRangoAdapterTest {
    @TestConfiguration
    @EnableJpaAuditing
    static class Auditoria { }

    static final OffsetDateTime DIA = OffsetDateTime.parse("2025-03-01T00:00:00Z");

    @Autowired JpaCuentaMovimientoRepository movimientos;
    @Autowired JpaCuentaMovimientoRepositoryAdapter adapter;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void cargar() {
        // El esquema de prueba crea claves foráneas por las relaciones de la entidad; acá sólo importan las columnas.
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbc.update("DELETE FROM movcon");
        // Desordenadas a propósito; cada columna con un valor distinto para detectar argumentos cruzados.
        movimiento(2, 1, "30101010001", (byte) 0, "20.00", "segundo asiento", 31, 71, (byte) 0);
        movimiento(1, 2, "21101010001", (byte) 0, "10.00", "haber", 32, 72, (byte) 1);
        movimiento(1, 1, "11101010001", (byte) 1, "10.00", "debe", 33, 73, (byte) 0);
    }

    @AfterEach
    void restaurar() {
        jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    void movimiento(int orden, int item, String cuenta, byte debita, String importe, String concepto,
                    int comprobanteId, int proveedorId, byte apertura) {
        movimiento(orden, item, cuenta, debita, importe, concepto, comprobanteId, proveedorId, apertura, DIA);
    }

    void movimiento(int orden, int item, String cuenta, byte debita, String importe, String concepto,
                    int comprobanteId, int proveedorId, byte apertura, OffsetDateTime fecha) {
        movimientos.saveAndFlush(CuentaMovimientoEntity.builder()
                .fechaContable(fecha).ordenContable(orden).item(item).numeroCuenta(new BigDecimal(cuenta))
                .debita(debita).importe(new BigDecimal(importe)).concepto(concepto)
                .comprobanteId(comprobanteId).proveedorId(proveedorId).apertura(apertura).build());
    }

    @Test
    void ordenaPorAsientoEItemYCopiaCadaColumna() {
        List<LineaMovimiento> lineas = adapter.findLineasByFechaContable(DIA, DIA.plusDays(1), 10);

        assertThat(lineas).extracting(LineaMovimiento::ordenContable, LineaMovimiento::item)
                .containsExactly(tuple(1, 1), tuple(1, 2), tuple(2, 1));
        LineaMovimiento primera = lineas.getFirst();
        assertThat(primera.cuentaMovimientoId()).isNotNull();
        assertThat(primera.fechaContable().toInstant()).isEqualTo(DIA.toInstant());
        assertThat(primera.numeroCuenta()).isEqualByComparingTo("11101010001");
        assertThat(primera.debita()).isEqualTo((byte) 1);
        assertThat(primera.importe()).isEqualByComparingTo("10.00");
        assertThat(primera.concepto()).isEqualTo("debe");
        assertThat(primera.comprobanteId()).isEqualTo(33);
        assertThat(primera.proveedorId()).isEqualTo(73);
        assertThat(primera.apertura()).isEqualTo((byte) 0);
        assertThat(lineas.get(1).apertura()).isEqualTo((byte) 1);
        assertThat(lineas.get(1).comprobanteId()).isEqualTo(32);
        assertThat(lineas.get(1).proveedorId()).isEqualTo(72);
    }

    @Test
    void lineasDeUnAsientoSoloDeEseOrdenYEnOrdenDeItem() {
        movimiento(1, 3, "11101010002", (byte) 1, "5.00", "otro dia", 34, 74, (byte) 0, DIA.plusDays(1));

        assertThat(adapter.findLineasByAsiento(DIA, DIA.plusDays(1), 1))
                .extracting(LineaMovimiento::ordenContable, LineaMovimiento::item, LineaMovimiento::concepto)
                .containsExactly(tuple(1, 1, "debe"), tuple(1, 2, "haber"));
        assertThat(adapter.findLineasByAsiento(DIA, DIA.plusDays(1), 3)).isEmpty();
    }

    @Test
    void elLimiteCortaEnElOrdenDelAdaptador() {
        assertThat(adapter.findLineasByFechaContable(DIA, DIA.plusDays(1), 2))
                .extracting(LineaMovimiento::ordenContable, LineaMovimiento::item)
                .containsExactly(tuple(1, 1), tuple(1, 2));
    }
}
