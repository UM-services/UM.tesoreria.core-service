package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobDetalle;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.FiltroEnvio;

import java.util.List;

/** Operaciones masivas del envío de chequeras por facultad y sede. */
public interface EnvioMasivoUseCase {

    EnvioJob iniciar(FiltroEnvio filtro, Long usuarioId);

    EnvioJobDetalle consultar(Long jobId);

    EnvioJob cancelar(Long jobId);

    EnvioJob reintentar(Long jobId, boolean soloFallidas);

    List<EnvioJob> pendientes();
}
