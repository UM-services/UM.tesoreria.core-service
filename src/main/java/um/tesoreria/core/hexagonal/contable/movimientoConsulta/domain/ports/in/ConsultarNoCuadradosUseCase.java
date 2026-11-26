package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientosNoCuadrados;

import java.time.OffsetDateTime;

public interface ConsultarNoCuadradosUseCase {

    AsientosNoCuadrados consultarNoCuadrados(OffsetDateTime desde, OffsetDateTime hasta);
}
