package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobDetalle;

public interface ConsultarJobAsientoIngresoUseCase {

    AsientoIngresoJobDetalle consultar(Long jobId);

}
