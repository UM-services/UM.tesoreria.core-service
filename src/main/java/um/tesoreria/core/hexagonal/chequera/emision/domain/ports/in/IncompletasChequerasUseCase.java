package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroIncompletas;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobDetalle;

import java.util.List;

/**
 * Reparación masiva de chequeras incompletas ({@code frmIncompletas}): etapa de borradores y
 * etapa de emisión, con consulta y cancelación del job.
 */
public interface IncompletasChequerasUseCase {

    /** Etapa A: crea/reutiliza un borrador por cada serie incompleta del padrón. */
    IncompletasJob iniciarBorradores(FiltroIncompletas filtro, Long usuarioId);

    /** Etapa B: emite los borradores de la etapa A (todos o un subconjunto). */
    IncompletasJob iniciarEmision(Long jobOrigenId, List<Long> borradorIds, Long usuarioId);

    IncompletasJobDetalle consultar(Long jobId);

    IncompletasJob cancelar(Long jobId);

    List<IncompletasJob> pendientes();
}
