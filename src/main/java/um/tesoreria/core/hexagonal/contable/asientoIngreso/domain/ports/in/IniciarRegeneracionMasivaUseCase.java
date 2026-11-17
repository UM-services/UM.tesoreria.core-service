package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;

import java.time.OffsetDateTime;

public interface IniciarRegeneracionMasivaUseCase {

    AsientoIngresoJob iniciar(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Long usuarioId);

}
