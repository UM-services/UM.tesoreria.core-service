package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;

import java.time.OffsetDateTime;

public interface ConsultarAsientoIngresoUseCase {

    AsientoIngresoResumen consultar(OffsetDateTime fecha, Integer tipoPagoId);

}
