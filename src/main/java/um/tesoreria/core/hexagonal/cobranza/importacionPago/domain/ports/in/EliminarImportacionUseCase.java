package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoEliminacion;

public interface EliminarImportacionUseCase {

    ResultadoEliminacion eliminar(Integer importacionId);

}
