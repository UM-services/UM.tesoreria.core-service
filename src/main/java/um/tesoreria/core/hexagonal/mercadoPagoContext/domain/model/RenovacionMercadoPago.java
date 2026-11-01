package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Candidato detectado por el core: un contexto activo cuyo vencimiento publicado ya no es el
 * que corresponde y existe un vencimiento posterior para publicar.
 */
public record RenovacionMercadoPago(MercadoPagoContext contexto,
                                    ChequeraCuota chequeraCuota,
                                    OffsetDateTime fechaVencimientoDestino,
                                    BigDecimal importeDestino) {

    public boolean requiereActualizacion() {
        if (contexto == null || fechaVencimientoDestino == null || importeDestino == null) {
            return false;
        }
        boolean mismaFecha = fechaVencimientoDestino.isEqual(contexto.getFechaVencimiento());
        boolean mismoImporte = contexto.getImporte() != null
                && importeDestino.compareTo(contexto.getImporte()) == 0;
        return !mismaFecha || !mismoImporte;
    }
}
