package um.tesoreria.core.service.facade.mayor;

import java.time.OffsetDateTime;
import java.util.List;

/** Un bloque por cuenta, ordenado por número de cuenta. */
public record MayorConsultaResponse(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<MayorCuentaResponse> cuentas) {
}