package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record NoRegistradoResponse(
        Long id,
        String codigoEmpresa,
        Integer identificacion,
        Integer productoId,
        Integer mesPago,
        Integer anhoPago,
        Integer cuotaId,
        BigDecimal importe,
        OffsetDateTime fechaPago,
        String marcaAcreditacion,
        OffsetDateTime fechaAcreditacion,
        Integer tipoPagoId) {
}
