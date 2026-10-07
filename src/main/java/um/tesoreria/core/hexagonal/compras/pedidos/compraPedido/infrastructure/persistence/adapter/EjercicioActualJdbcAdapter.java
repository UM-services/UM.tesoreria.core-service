package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.EjercicioActual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.EjercicioActualPort;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Resuelve el ejercicio contable vigente leyendo la tabla {@code ejercicios}.
 *
 * <p>Se resuelve con SQL directo para no acoplar el slice al modelo legacy
 * {@code um.tesoreria.core.kotlin.model.Ejercicio}. El ejercicio es un dato de referencia
 * del servicio, no de otro servicio.</p>
 */
@Component
@RequiredArgsConstructor
public class EjercicioActualJdbcAdapter implements EjercicioActualPort {

    private static final String SQL = """
            SELECT eje_id, YEAR(eje_fechainicio) AS anio
            FROM ejercicios
            WHERE eje_fechainicio <= ? AND eje_fechafin >= ?
            ORDER BY eje_id DESC
            LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<EjercicioActual> findEjercicioActual(LocalDateTime fecha) {
        List<EjercicioActual> result = jdbcTemplate.query(SQL,
                (rs, rowNum) -> new EjercicioActual(rs.getInt("eje_id"), rs.getInt("anio")),
                Timestamp.valueOf(fecha), Timestamp.valueOf(fecha));
        return result.stream().findFirst();
    }

}
