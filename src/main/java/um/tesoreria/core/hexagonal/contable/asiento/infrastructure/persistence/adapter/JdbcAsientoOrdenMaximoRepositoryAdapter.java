package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoOrdenMaximoRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Siembra de la secuencia con el máximo que ya existe en cualquiera de las fuentes donde el
 * legacy reserva un orden antes de escribir {@code movcon}. Se usa JDBC porque dos de esas
 * fuentes ({@code ingreso_asiento}, {@code valores}) no tienen un repositorio de máximo en el
 * slice y la consulta es de sólo lectura.
 */
@Component
@RequiredArgsConstructor
public class JdbcAsientoOrdenMaximoRepositoryAdapter implements AsientoOrdenMaximoRepository {

    private static final String MAX_ASIENTO =
            "SELECT COALESCE(MAX(orden), 0) FROM asiento WHERE fecha = :fecha";
    private static final String MAX_MOVCON =
            "SELECT COALESCE(MAX(MCo_NroComp), 0) FROM movcon WHERE MCo_Fecha = :fecha";
    private static final String MAX_INGRESO_ASIENTO =
            "SELECT COALESCE(MAX(ina_nrocomp), 0) FROM ingreso_asiento WHERE ina_fecha = :fecha";
    private static final String MAX_VALORES =
            "SELECT COALESCE(MAX(Val_NumeroAsiento), 0) FROM valores WHERE Val_FechaAsiento = :fecha";
    private static final String MAX_EJERCICIO =
            "SELECT COALESCE(MAX(GREATEST(orden_contable_resultado, orden_contable_bienes_uso)), 0) "
                    + "FROM ejercicios WHERE Eje_FechaFin = :fecha";

    private final JdbcClient jdbcClient;

    @Override
    public int maxOrden(OffsetDateTime fecha) {
        LocalDateTime contable = fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
        return Math.max(
                Math.max(max(MAX_ASIENTO, contable), max(MAX_MOVCON, contable)),
                Math.max(Math.max(max(MAX_INGRESO_ASIENTO, contable), max(MAX_VALORES, contable)),
                        max(MAX_EJERCICIO, contable)));
    }

    private int max(String sql, LocalDateTime fecha) {
        Integer valor = jdbcClient.sql(sql)
                .param("fecha", fecha)
                .query(Integer.class)
                .single();
        return valor == null ? 0 : valor;
    }

}
