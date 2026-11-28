package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.MovimientosContables;

import java.time.OffsetDateTime;

public interface ConsultarMovimientosUseCase {

    MovimientosContables consultarMovimientos(OffsetDateTime desde, OffsetDateTime hasta);
}
