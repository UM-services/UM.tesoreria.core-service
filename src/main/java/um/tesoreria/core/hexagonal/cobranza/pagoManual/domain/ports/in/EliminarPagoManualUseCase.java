package um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.ResultadoEliminacionPago;

public interface EliminarPagoManualUseCase {

    ResultadoEliminacionPago eliminar(Long chequeraPagoId);

}
