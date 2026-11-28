package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroGeneracion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobDetalle;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionPadronItem;

import java.util.List;

/**
 * Generación masiva de borradores sobre las chequeras de una facultad y sede y emisión previa al
 * envío masivo.
 */
public interface GeneracionBorradoresUseCase {

    /** Vista previa del padrón de series que se van a procesar. */
    List<GeneracionPadronItem> preview(FiltroGeneracion filtro);

    /** Etapa A: crea o reutiliza un borrador por cada serie del padrón. */
    GeneracionJob iniciarBorradores(FiltroGeneracion filtro, Long usuarioId);

    /** Etapa B: emite los borradores de la etapa A (todos o un subconjunto). */
    GeneracionJob iniciarEmision(Long jobOrigenId, List<Long> borradorIds, boolean forzarDeuda,
                                 String motivoOverride, Long usuarioId);

    /** Etapa B sin job de origen: emite los borradores abiertos del filtro (pantalla de envío). */
    GeneracionJob iniciarEmisionDirecta(FiltroGeneracion filtro, Long usuarioId);

    GeneracionJobDetalle consultar(Long jobId);

    GeneracionJob cancelar(Long jobId);

    List<GeneracionJob> pendientes();
}
