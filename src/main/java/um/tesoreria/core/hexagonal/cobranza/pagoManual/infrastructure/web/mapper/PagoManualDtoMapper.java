package um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.PagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.ResultadoEliminacionPago;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto.PagoManualResponse;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto.ResultadoEliminacionPagoResponse;

@Component
public class PagoManualDtoMapper {

    public PagoManualResponse toResponse(PagoManual pago) {
        if (pago == null) {
            return null;
        }
        return new PagoManualResponse(pago.chequeraPagoId(), pago.chequeraCuotaId(),
                pago.facultadId(), pago.tipoChequeraId(), pago.chequeraSerieId(), pago.productoId(),
                pago.alternativaId(), pago.cuotaId(), pago.fecha(), pago.importe(),
                pago.observaciones(), pago.tipoPagoId());
    }

    public ResultadoEliminacionPagoResponse toResponse(ResultadoEliminacionPago resultado) {
        if (resultado == null) {
            return null;
        }
        return new ResultadoEliminacionPagoResponse(resultado.chequeraPagoId(), resultado.eliminado());
    }

}
