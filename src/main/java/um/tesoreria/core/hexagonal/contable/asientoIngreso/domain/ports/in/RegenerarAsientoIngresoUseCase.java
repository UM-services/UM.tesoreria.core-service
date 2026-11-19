package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;

import java.time.OffsetDateTime;

public interface RegenerarAsientoIngresoUseCase {

    AsientoIngresoResultado regenerar(OffsetDateTime fecha, Integer tipoPagoId);

}
