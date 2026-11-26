package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;

import java.time.OffsetDateTime;

public interface ConsultarAsientoUseCase {

    AsientoDiario consultarAsiento(OffsetDateTime fecha, Integer orden);
}
