package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

/** Asientos no cuadrados de un rango, en orden de fecha y orden. */
public record AsientosNoCuadrados(OffsetDateTime desde, OffsetDateTime hasta, List<AsientoNoCuadrado> asientos) {
}
