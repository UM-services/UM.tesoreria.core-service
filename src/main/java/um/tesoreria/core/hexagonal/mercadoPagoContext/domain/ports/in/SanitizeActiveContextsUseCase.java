package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.SanitizacionContexto;

import java.util.List;

public interface SanitizeActiveContextsUseCase {

    /**
     * Garantiza un unico contexto activo por cuota: conserva el de mayor id y desactiva el resto.
     *
     * @param dryRun si es true solo informa que haria, sin escribir
     */
    List<SanitizacionContexto> sanitize(boolean dryRun);

}
