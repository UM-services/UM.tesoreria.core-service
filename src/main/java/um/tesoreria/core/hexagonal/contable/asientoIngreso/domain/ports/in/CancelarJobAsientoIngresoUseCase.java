package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;

public interface CancelarJobAsientoIngresoUseCase {

    AsientoIngresoJob cancelar(Long jobId);

}
