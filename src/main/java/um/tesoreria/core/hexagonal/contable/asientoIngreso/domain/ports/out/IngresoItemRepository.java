package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoLinea;

import java.time.OffsetDateTime;
import java.util.List;

/** Lee la tabla legacy {@code ingreso_items} (sólo lectura: no hay ABM en el sistema). */
public interface IngresoItemRepository {

    List<AsientoIngresoLinea> findAllByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId);

}
