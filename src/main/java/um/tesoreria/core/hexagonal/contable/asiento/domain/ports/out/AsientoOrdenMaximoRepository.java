package um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out;

import java.time.OffsetDateTime;

/**
 * Máximo orden contable ya usado en una fecha, mirando <b>todas</b> las fuentes donde el legacy
 * reserva un número antes de escribir {@code movcon}:
 * {@code asiento}, {@code movcon}, {@code ingreso_asiento} y {@code valores}.
 */
public interface AsientoOrdenMaximoRepository {

    int maxOrden(OffsetDateTime fecha);

}
