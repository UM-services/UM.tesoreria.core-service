package um.tesoreria.core.hexagonal.contable.asiento;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoOrdenMaximoRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contraste del {@code maxOrden} con el máximo real de las cinco fuentes (asiento, movcon,
 * ingreso_asiento, valores y cierres del ejercicio) usando el SQL anterior como oráculo: el
 * adaptador se migró de JDBC a Criteria y tiene que devolver exactamente lo mismo.
 */
@SpringBootTest
@ActiveProfiles("it")
@Transactional(readOnly = true)
class AsientoOrdenMaximoIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private AsientoOrdenMaximoRepository maximoRepository;

    @Test
    void coincideConElMaximoDeLasCincoFuentes() {
        LocalDateTime fecha = jdbc.queryForList("""
                        SELECT fecha FROM asiento GROUP BY fecha ORDER BY COUNT(*) DESC LIMIT 1
                        """, LocalDateTime.class)
                .stream().findFirst().orElse(null);
        assumeTrue(fecha != null, "La base de este entorno no tiene asientos");

        int esperado = Math.max(
                Math.max(max("SELECT COALESCE(MAX(orden), 0) FROM asiento WHERE fecha = ?", fecha),
                        max("SELECT COALESCE(MAX(MCo_NroComp), 0) FROM movcon WHERE MCo_Fecha = ?", fecha)),
                Math.max(
                        Math.max(max("SELECT COALESCE(MAX(ina_nrocomp), 0) FROM ingreso_asiento WHERE ina_fecha = ?", fecha),
                                max("SELECT COALESCE(MAX(Val_NumeroAsiento), 0) FROM valores WHERE Val_FechaAsiento = ?", fecha)),
                        max("SELECT COALESCE(MAX(GREATEST(orden_contable_resultado, orden_contable_bienes_uso)), 0) "
                                + "FROM ejercicios WHERE Eje_FechaFin = ?", fecha)));

        assertThat(maximoRepository.maxOrden(fecha.atOffset(ZoneOffset.UTC))).isEqualTo(esperado);
    }

    @Test
    void unaFechaSinDatosDevuelveCero() {
        OffsetDateTime sinDatos = OffsetDateTime.of(2050, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertThat(maximoRepository.maxOrden(sinDatos)).isZero();
    }

    private int max(String sql, LocalDateTime fecha) {
        Integer valor = jdbc.queryForObject(sql, Integer.class, fecha);
        return valor == null ? 0 : valor;
    }

}
