package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;

import java.time.OffsetDateTime;

public interface ConsultarLibroDiarioUseCase {

    LibroDiario consultarLibroDiario(OffsetDateTime desde, OffsetDateTime hasta);
}
